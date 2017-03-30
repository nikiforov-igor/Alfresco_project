/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.descriptor.DescriptorService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.license.LicenseDescriptor;

/**
 * Реестр lecm сервисов, на момент написания нужен только для обеспечения
 * централизованной инициализации сервисов
 * 
 * @author ikhalikov
 */
public class LecmServicesRegistryImpl extends AbstractLifecycleBean implements LecmServicesRegistry {

	private static final Logger logger = LoggerFactory.getLogger(LecmServicesRegistryImpl.class);

	private LecmTransactionHelper lecmTransactionHelper;
	private SysAdminParams sysAdminParams;
	private DescriptorService descriptorService;
	private LecmBaseNamesService namesService;
	private final List<LecmService> services = new ArrayList<>();

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}

	public void setDescriptorService(DescriptorService descriptorService) {
		this.descriptorService = descriptorService;
	}

	public void setSysAdminParams(SysAdminParams sysAdminParams){
		this.sysAdminParams = sysAdminParams;
	}

	public void setNamesService(LecmBaseNamesService namesService){
		this.namesService = namesService;
	}

	/**
	 * Сохранение сервиса в списке, в качестве ключа берётся имя класса
	 * @param service 
	 */
	@Override
	public void register(LecmService service) {
		services.add(service);
	}
	
	/**
	 * Централизованная инициализация всех сервисов
	 * @param ae 
	 */
	@Override
	protected void onBootstrap(ApplicationEvent ae) {
		Object propertyName = namesService.getPropertyName("lecm.activation.location");
		if (propertyName == null) {
			makeFile();
			throw new NullPointerException();
		}
		else {
			AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
				@Override
				public Void doWork() throws Exception {
					return lecmTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
						@Override
						public Void execute() throws Throwable {
							for (LecmService service : services) {
								if (logger.isDebugEnabled()) {
									logger.debug("Going to bootstrap service {}", service);
								}
								service.initService();
							}

							return null;
						}
					}, false);
				}
			});
		}
	}

	@Override
	protected void onShutdown(ApplicationEvent ae) {
	}

	private void makeFile() {
		StringBuilder sb = new StringBuilder();
		String currentHash = "";
		sb.append("#------------------------------ SysAdminInfo ------------------------------\n");
		try {
			sb.append("#Network interface: ");
			NetworkInterface netint = NetworkInterface.getByInetAddress(InetAddress.getByName(sysAdminParams.getAlfrescoHost()));
			if (netint != null) currentHash = displayInterfaceInformation(netint);
			if (!"".equals(currentHash)) sb.append(currentHash);
			else {
				sb.append("WARNING");
				if (netint != null) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						if (inetAddress instanceof Inet4Address) {
							sb.append(" - ");
							sb.append(inetAddress.getHostAddress());
						}
					}
				}
				sb.append("\n");
				Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
				while (nis.hasMoreElements()) {
					currentHash = displayInterfaceInformation(nis.nextElement());
					if (!"".equals(currentHash)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			sb.append("#").append(e);
			sb.append("\n");
		}
		sb.append("#MaxUsers: ");
		sb.append(sysAdminParams.getMaxUsers());
		sb.append("\n");
		sb.append("#AllowWrite: ");
		sb.append(sysAdminParams.getAllowWrite());
		sb.append("\n");
		sb.append("#AlfrescoHost: ");
		sb.append(sysAdminParams.getAlfrescoHost());
		sb.append("\n");
		sb.append("#Processors: ");
		sb.append(Runtime.getRuntime().availableProcessors());
		sb.append("\n");
		LicenseDescriptor license = descriptorService.getLicenseDescriptor();
		if (license != null) {
			sb.append("#License mode: ");
			sb.append(license.getLicenseMode());
			sb.append("\n");
		}
		Descriptor currentDescriptor = descriptorService.getCurrentRepositoryDescriptor();
		sb.append("#Current mode: ");
		sb.append(currentDescriptor.getLicenseMode());
		sb.append("\n");
		sb.append("hash=");
		sb.append(currentHash);
		sb.append("\n");
		sb.append("uuid=");
		sb.append(currentDescriptor.getId());
		sb.append("\n");
		String activation = Base64.encodeBase64String(sb.toString().getBytes());

		FileOutputStream fop = null;
		File file;
		try {
			file = new File("./activation");
			if (!file.exists()) {
				file.createNewFile();
				fop = new FileOutputStream(file);
				fop.write(activation.getBytes());
				fop.flush();
				fop.close();
			}
		} catch (IOException e) {
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
		byte[] mac = netint.getHardwareAddress();
		if(mac!=null) {
			Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
			for(InetAddress inetAddress:Collections.list(inetAddresses)) {
				if(inetAddress instanceof Inet4Address) {
					StringBuilder sb = new StringBuilder();
					for(int i=0;i<mac.length;i++) {
						sb.append(String.format("%02x%s", mac[i], (i < mac.length - 1) ? "" : ""));
					}
					return sb.toString();
				}
			}
		}
		return "";
	}

}
