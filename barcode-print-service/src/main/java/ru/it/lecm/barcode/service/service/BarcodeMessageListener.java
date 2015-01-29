package ru.it.lecm.barcode.service.service;

import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.it.lecm.barcode.service.entity.PrintJob;
import ru.it.lecm.barcode.service.entity.PrintResult;

/**
 *
 * @author vlevin
 */
public class BarcodeMessageListener implements MessageListener {

	private final static Logger logger = LoggerFactory.getLogger(BarcodeMessageListener.class);

	@Autowired
	private PrintService printService;

	@Override
	public void onMessage(Message msg) {
		try {
			PrintJob job = (PrintJob) ((ActiveMQObjectMessage) msg).getObject();
			PrintResult printResult = printService.print(job);
			if (printResult.isSuccess()) {
				msg.acknowledge();
			}
		} catch (IOException | RuntimeException | JMSException ex) {
			logger.error("Error!", ex);
		}
	}

}
