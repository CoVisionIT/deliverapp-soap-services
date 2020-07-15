package be.deliverapp.despatchadvice.mapper


import be.covisionit.deliverapp.proto.*
import be.covisionit.deliverapp.proto.DeliveryNoteReceptionPreferences.*
import be.deliverapp.despatchadvice.DeliveryNoteReceptionPreferencesDeliveryNoteFormat
import com.google.protobuf.Timestamp
import java.util.concurrent.atomic.AtomicInteger

object DespatchAdviceMapper {

    fun map(d: be.deliverapp.despatchadvice.DespatchAdvice?): DespatchAdvice {

        val lineNr = AtomicInteger(1)

        val lines: List<DespatchLine> = d
                ?.despatchLines
                ?.despatchLine
                ?.filter { !it?.item?.description.isNullOrEmpty() }
                ?.mapNotNull { line ->
                    DespatchLine.newBuilder()
                            .setID(line.id?.ifBlank { lineNr.getAndIncrement().toString() })
                            .setItem(Item.newBuilder().setDescription(line.item?.description).build())
                            .setDeliveredQuantity(if (line.deliveredQuantity == null) 1.0 else line.deliveredQuantity)
                            .setDeliveredQuantityUnitCode(if (line.deliveredQuantityUnitCode.isNullOrBlank()) "EA" else line.deliveredQuantityUnitCode)
                            .build()
                } ?: listOf()

        val emailPrefBuilder = EmailPreferences.newBuilder()
        val emailPrefs = d?.despatchSupplierParty?.deliveryNoteReceptionPreferences?.emailPreferences
        emailPrefBuilder.active = emailPrefs?.isActive ?: false
        if (emailPrefBuilder.active) {
            emailPrefs?.emailAddress?.let { emailPrefBuilder.emailAddress = it }
            findFormat(emailPrefs?.format)?.let { emailPrefBuilder.format = it }
        }

        val endpointPrefBuilder = EndpointPreferences.newBuilder()
        val endpointPrefs = d?.despatchSupplierParty?.deliveryNoteReceptionPreferences?.endpointPreferences
        endpointPrefBuilder.active = endpointPrefs?.isActive ?: false
        if (endpointPrefBuilder.active) {
            endpointPrefs?.url?.let { endpointPrefBuilder.url = it }
            findFormat(endpointPrefs?.format)?.let { endpointPrefBuilder.format = it }
        }

        val despatch = Despatch.newBuilder()
                .setDespatchAddress(Address.newBuilder()
                        .setID(d?.shipment?.delivery?.despatch?.despatchAddress?.id.orEmpty())
                        .setStreetName(d?.shipment?.delivery?.despatch?.despatchAddress?.streetName.orEmpty())
                        .setPostalZone(d?.shipment?.delivery?.despatch?.despatchAddress?.postalZone.orEmpty())
                        .setCityName(d?.shipment?.delivery?.despatch?.despatchAddress?.cityName.orEmpty())
                        .setCountry(d?.shipment?.delivery?.despatch?.despatchAddress?.country.orEmpty())
                )
        d?.shipment?.delivery?.despatch?.actualDespatchTimestamp?.let { value ->
            if (value.seconds > 0 || value.nanos > 0) {
                despatch.actualDespatchTimestamp =
                        Timestamp.newBuilder()
                                .setSeconds(value.seconds)
                                .setNanos(value.nanos)
                                .build()
            }
        }

        return DespatchAdvice.newBuilder()
                .setID(d?.id.orEmpty())
                .setOrderReference(OrderReference.newBuilder()
                        .setID(d?.orderReference?.id.orEmpty())
                )
                .setDespatchSupplierParty(DespatchSupplierParty.newBuilder()
                        .setParty(Party.newBuilder()
                                .setPartyIdentification(PartyIdentification.newBuilder()
                                        .setID(required(d?.despatchSupplierParty?.party?.partyIdentification?.id, "Supplier VAT", "/DespatchSupplierParty/Party/PartyIdentification[@ID]"))
                                )
                                .setPartyName(PartyName.newBuilder()
                                        .setName(required(d?.despatchSupplierParty?.party?.partyName?.name, "Supplier Name", "/DespatchSupplierParty/Party/PartyName[@Name]"))
                                )
                                .setContact(Contact.newBuilder()
                                        .setTelephone(d?.despatchSupplierParty?.party?.contact?.telephone.orEmpty())
                                )
                        )
                        .setDeliveryNoteReceptionPreferences(newBuilder()
                                .setEmailPreferences(emailPrefBuilder)
                                .setEndpointPreferences(endpointPrefBuilder)
                        )
                )
                .setDeliveryCustomerParty(DeliveryCustomerParty.newBuilder()
                        .setParty(Party.newBuilder()
                                .setPartyIdentification(PartyIdentification.newBuilder()
                                        .setID(required(d?.deliveryCustomerParty?.party?.partyIdentification?.id, "Customer VAT", "/DeliveryCustomerParty/Party/PartyIdentification[@ID]"))
                                )
                                .setPartyName(PartyName.newBuilder()
                                        .setName(required(d?.deliveryCustomerParty?.party?.partyName?.name, "Customer Name", "/DeliveryCustomerParty/Party/PartyName[@Name]"))
                                )
                                .setContact(Contact.newBuilder()
                                        .setTelephone(d?.deliveryCustomerParty?.party?.contact?.telephone.orEmpty())
                                )
                        )
                )
                .setShipment(Shipment.newBuilder()
                        .setID(required(d?.shipment?.id, "Delivery Note Number", "/Shipment[@ID]"))
                        .setConsignment(Consignment.newBuilder()
                                .setInformation(d?.shipment?.consignment?.information.orEmpty())
                                .setCarrierParty(Party.newBuilder()
                                        .setPartyName(PartyName.newBuilder()
                                                .setName(d?.shipment?.consignment?.carrierParty?.partyName?.name.orEmpty())
                                        )
                                )
                        )
                        .setDelivery(Delivery.newBuilder()
                                .setDespatch(despatch)
                        )
                )
                .addAllDespatchLine(lines)
                .build()
    }

    private fun required(value: String?, name: String, path: String): String {
        if (value != null) {
            return value
        } else {
            throw Exception("$name is required ($path)")
        }
    }

    private fun findFormat(formatTypeStringOpt: DeliveryNoteReceptionPreferencesDeliveryNoteFormat?): DeliveryNoteFormat? {
        return formatTypeStringOpt?.let {
            return try {
                DeliveryNoteFormat.valueOf(it.value())
            } catch (e: Exception) {
                null
            }
        }
    }


}
