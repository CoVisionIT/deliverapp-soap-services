# How to generate despatchadvice.xsd

First create the protocol buffer message definition
```
protoc --include_imports --descriptor_set_out=despatchadvice.desc src/main/proto/be/covisionit/deliverapp/despatchadvice.proto
```

Then use the [protobuf-to-xsd](https://github.com/halex2005/protobuf-to-xsd) tool to generate the xsd file
```
protogen.exe -i:despatchadvice.desc -o:despatchadvice.xsd -t:xsd-attributes -d
```

The output xsd needs some manual fixes:

```
DeliveryNoteReceptionPreferences.DeliveryNoteFormat -> DeliveryNoteFormat

name=". -> name="

type="" -> type="xs:double"

xmlns:tns="http://deliverapp.be/despatchadvice"
xmlns="http://deliverapp.be/despatchadvice"
targetNamespace="http://deliverapp.be/despatchadvice"

<xs:simpleType name="DeliveryNoteReceptionPreferences.DeliveryNoteFormat">
  <xs:restriction base="xs:string">
    <xs:enumeration value="JSON" />
    <xs:enumeration value="PEPPOL" />
    <xs:enumeration value="UBL" />
  </xs:restriction>
</xs:simpleType>
```

Add requests and response types
```
  <xs:element name="getQRBinaryContentRequest">
    <xs:complexType>
      <xs:all>
        <xs:element name="despatchAdvice" type="DespatchAdvice"/>
      </xs:all>
    </xs:complexType>
  </xs:element>
    ... (see current despatchadvice.xsd)
```
