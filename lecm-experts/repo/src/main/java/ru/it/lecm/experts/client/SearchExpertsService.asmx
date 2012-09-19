<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/">
      <s:element name="requestExpert">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="1" maxOccurs="1" name="id" type="s:int" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="requestExpertResult">
              <s:complexType mixed="true">
                <s:sequence>
                  <s:any />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExperts">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="query" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="requestExpertsResult">
              <s:complexType mixed="true">
                <s:sequence>
                  <s:any />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsEx">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="binData" type="s:base64Binary" />
            <s:element minOccurs="0" maxOccurs="1" name="fileName" type="s:string" />
            <s:element minOccurs="1" maxOccurs="1" name="min" type="s:boolean" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsExResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="requestExpertsExResult">
              <s:complexType mixed="true">
                <s:sequence>
                  <s:any />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsByURI">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="uri" type="s:string" />
            <s:element minOccurs="1" maxOccurs="1" name="min" type="s:boolean" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsByURIResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="requestExpertsByURIResult">
              <s:complexType mixed="true">
                <s:sequence>
                  <s:any />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsCoefTerm">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="query" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="term" type="s:string" />
            <s:element minOccurs="1" maxOccurs="1" name="coef" type="s:float" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="requestExpertsCoefTermResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="requestExpertsCoefTermResult">
              <s:complexType mixed="true">
                <s:sequence>
                  <s:any />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="requestExpertSoapIn">
    <wsdl:part name="parameters" element="tns:requestExpert" />
  </wsdl:message>
  <wsdl:message name="requestExpertSoapOut">
    <wsdl:part name="parameters" element="tns:requestExpertResponse" />
  </wsdl:message>
  <wsdl:message name="requestExpertsSoapIn">
    <wsdl:part name="parameters" element="tns:requestExperts" />
  </wsdl:message>
  <wsdl:message name="requestExpertsSoapOut">
    <wsdl:part name="parameters" element="tns:requestExpertsResponse" />
  </wsdl:message>
  <wsdl:message name="requestExpertsExSoapIn">
    <wsdl:part name="parameters" element="tns:requestExpertsEx" />
  </wsdl:message>
  <wsdl:message name="requestExpertsExSoapOut">
    <wsdl:part name="parameters" element="tns:requestExpertsExResponse" />
  </wsdl:message>
  <wsdl:message name="requestExpertsByURISoapIn">
    <wsdl:part name="parameters" element="tns:requestExpertsByURI" />
  </wsdl:message>
  <wsdl:message name="requestExpertsByURISoapOut">
    <wsdl:part name="parameters" element="tns:requestExpertsByURIResponse" />
  </wsdl:message>
  <wsdl:message name="requestExpertsCoefTermSoapIn">
    <wsdl:part name="parameters" element="tns:requestExpertsCoefTerm" />
  </wsdl:message>
  <wsdl:message name="requestExpertsCoefTermSoapOut">
    <wsdl:part name="parameters" element="tns:requestExpertsCoefTermResponse" />
  </wsdl:message>
  <wsdl:portType name="SearchExpertsServiceSoap">
    <wsdl:operation name="requestExpert">
      <wsdl:input message="tns:requestExpertSoapIn" />
      <wsdl:output message="tns:requestExpertSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="requestExperts">
      <wsdl:input message="tns:requestExpertsSoapIn" />
      <wsdl:output message="tns:requestExpertsSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="requestExpertsEx">
      <wsdl:input message="tns:requestExpertsExSoapIn" />
      <wsdl:output message="tns:requestExpertsExSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="requestExpertsByURI">
      <wsdl:input message="tns:requestExpertsByURISoapIn" />
      <wsdl:output message="tns:requestExpertsByURISoapOut" />
    </wsdl:operation>
    <wsdl:operation name="requestExpertsCoefTerm">
      <wsdl:input message="tns:requestExpertsCoefTermSoapIn" />
      <wsdl:output message="tns:requestExpertsCoefTermSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="SearchExpertsServiceSoap" type="tns:SearchExpertsServiceSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="requestExpert">
      <soap:operation soapAction="http://tempuri.org/requestExpert" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExperts">
      <soap:operation soapAction="http://tempuri.org/requestExperts" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsEx">
      <soap:operation soapAction="http://tempuri.org/requestExpertsEx" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsByURI">
      <soap:operation soapAction="http://tempuri.org/requestExpertsByURI" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsCoefTerm">
      <soap:operation soapAction="http://tempuri.org/requestExpertsCoefTerm" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="SearchExpertsServiceSoap12" type="tns:SearchExpertsServiceSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="requestExpert">
      <soap12:operation soapAction="http://tempuri.org/requestExpert" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExperts">
      <soap12:operation soapAction="http://tempuri.org/requestExperts" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsEx">
      <soap12:operation soapAction="http://tempuri.org/requestExpertsEx" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsByURI">
      <soap12:operation soapAction="http://tempuri.org/requestExpertsByURI" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="requestExpertsCoefTerm">
      <soap12:operation soapAction="http://tempuri.org/requestExpertsCoefTerm" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="SearchExpertsService">
    <wsdl:port name="SearchExpertsServiceSoap" binding="tns:SearchExpertsServiceSoap">
      <soap:address location="http://172.28.0.185/SearchExpertsService.asmx" />
    </wsdl:port>
    <wsdl:port name="SearchExpertsServiceSoap12" binding="tns:SearchExpertsServiceSoap12">
      <soap12:address location="http://172.28.0.185/SearchExpertsService.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>