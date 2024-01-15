package com.r3.developers.samples.obligation.json

import com.r3.developers.samples.obligation.states.IOUState
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.ledger.utxo.query.json.ContractStateVaultJsonFactory

class IOUStateJsonFactory : ContractStateVaultJsonFactory<IOUState> {
    override fun getStateType(): Class<IOUState> = IOUState::class.java
    override fun create(state: IOUState, jsonMarshallingService: JsonMarshallingService): String {
        return jsonMarshallingService.format(
            IOUStateJson(
                state.amount,
                state.lender.toString(),
                state.borrower.toString(),
                state.paid,
                state.linearId.toString()
            )
        )
    }

    data class IOUStateJson(
        val amount: Int,
        val lender: String,
        val borrower: String,
        val paid: Int,
        val linearId: String
    )
}