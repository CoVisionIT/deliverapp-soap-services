# DeliverApp SOAP services
SOAP services to generate DeliverApp QR codes

### How to run the SOAP service

1. Download [deliverapp-soap-services-0.0.1.jar](artifacts/deliverapp-soap-services-0.0.1.jar).
2. Run `java -jar deliverapp-soap-services-0.0.1.jar` to start the [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/index.html) application
3. Find the web service description at http://localhost:8080/ws/despatchadvice.wsdl

The application runs default on port 8080, to change this, add `--server.port=9000`
to the startup command. Other command line options can be found 
[here](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#server-properties). 

### Example Requests

Hexadecimal representation of QR binary content:
```xml
<soapenv:Envelope 
        xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
        xmlns:da="http://deliverapp.be/despatchadvice">
   <soapenv:Header/>
   <soapenv:Body>
      <da:getQRBinaryContentRequest>
         <despatchAdvice>
            <OrderReference ID="purchase order"/>
            <DespatchSupplierParty>
               <Party>
                  <PartyIdentification ID="Supplier VAT"/>
                  <PartyName Name="Supplier Name"/>
                  <Contact Telephone="Supplier Phone"/>
               </Party>
               <deliveryNoteReceptionPreferences>
                  <emailPreferences active="true" format="JSON" emailAddress="supplier@deliverapp.be"/>
                  <endpointPreferences active="true" format="PEPPOL" url="https://supplier.deliverapp.be/da?single-use-token=sDEdxfz"/>
               </deliveryNoteReceptionPreferences>
            </DespatchSupplierParty>
            <DeliveryCustomerParty>
               <Party>
                  <PartyIdentification ID="Customer VAT"/>
                  <PartyName Name="Customer Name"/>
               </Party>
            </DeliveryCustomerParty>
            <Shipment ID="Delivery Note Number">
               <Consignment Information="Transporter License Plate">
                  <CarrierParty>
                     <PartyName Name="Transporter Name"/>
                  </CarrierParty>
               </Consignment>
               <Delivery>
                  <Despatch>
                     <ActualDespatchTimestamp seconds="1594844684" nanos="82000000"/>
                     <DespatchAddress ID="Site Reference" StreetName="Street Name + Number" CityName="City Name" PostalZone="Postal Code" Country="BE"/>
                  </Despatch>
               </Delivery>
            </Shipment>
            <DespatchLines>
               <DespatchLine ID="ITEM001" DeliveredQuantity="5" DeliveredQuantityUnitCode="EA">
                  <Item Description="some item"/>
               </DespatchLine>
               <DespatchLine ID="WIRE05" Note="?" DeliveredQuantity="1.15" DeliveredQuantityUnitCode="MTR">
                  <Item Description="wire"/>
               </DespatchLine>
            </DespatchLines>
         </despatchAdvice>
      </da:getQRBinaryContentRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

Base64 encoding of QR code PNG file:
```xml
<soapenv:Envelope 
       xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
       xmlns:da="http://deliverapp.be/despatchadvice">
   <soapenv:Header/>
   <soapenv:Body>
      <da:getQRImageRequest>
         <size>400</size>
         <despatchAdvice>
            <!-- (see previous example) -->
         </despatchAdvice>
      </da:getQRImageRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Test
  You can use the [DeliverApp QR Code Reader](https://deliverapp-endpoint.appspot.com/reader) to see if we can read the generated code and to check it's contents 
  
  or 
  
  Install the [DeliverApp](https://play.google.com/store/apps/details?id=be.covisionit.deliverapp) mobile application 
  on an Android device and scan your code
  (The iPhone/iPad version is still in development)
