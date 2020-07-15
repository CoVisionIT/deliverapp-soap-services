package be.deliverapp.despatchadvice

object TestData {
    fun buildTestDespatchAdvice(): DespatchAdvice? {
        return DespatchAdvice().apply {
            shipment = Shipment().apply {
                id = "delivery note number"
            }
            despatchSupplierParty = DespatchSupplierParty().apply {
                party = Party().apply {
                    partyIdentification = PartyIdentification().apply {
                        id = "SupplierVAT"
                    }
                    partyName = PartyName().apply {
                        name = "SupplierName"
                    }
                }
            }
            deliveryCustomerParty = DeliveryCustomerParty().apply {
                party = Party().apply {
                    partyIdentification = PartyIdentification().apply {
                        id = "CustomerVAT"
                    }
                    partyName = PartyName().apply {
                        name = "CustomerName"
                    }
                }
            }
        }
    }
}
