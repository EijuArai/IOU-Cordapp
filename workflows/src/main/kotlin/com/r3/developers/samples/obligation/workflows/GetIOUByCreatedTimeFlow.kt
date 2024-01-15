package com.r3.developers.samples.obligation.workflows

import com.r3.developers.samples.obligation.states.IOUState
import net.corda.v5.application.flows.*
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.ledger.utxo.UtxoLedgerService
import org.slf4j.LoggerFactory

@InitiatingFlow(protocol = "get-iou")
@Suppress("unused")
class GetIOUByCreatedTimeFlow : ClientStartableFlow {

    @CordaInject
    private lateinit var jsonMarshallingService: JsonMarshallingService

    @CordaInject
    private lateinit var utxoLedgerService: UtxoLedgerService

    @CordaInject
    lateinit var flowEngine: FlowEngine

    data class GetIOURequest(
        val from: Long,
        val to: Long
    )

    data class GetIOUResponse(
        val IOUs: List<IOUData>
    )

    data class IOUData(
        val amount: Int,
        val lender: String,
        val borrower: String,
        val paid: Int,
        val linearId: String
    )

    private companion object {
        val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @Suspendable
    override fun call(requestBody: ClientRequestBody): String {
        log.info("GetIOUByCreatedTimeFlow.call() called")

        log.info("Request body: {}", requestBody)
        val request = requestBody.getRequestBodyAs(jsonMarshallingService, GetIOURequest::class.java)

        log.info("Querying IOU")
        val results = utxoLedgerService.query("GET_BY_CREATED_TIME", IOUState::class.java)
            .setParameter("from", request.from)
            .setParameter("to", request.to)
            .setLimit(100)
            .execute()
            .results

        require(results.isNotEmpty()) {
            "IOU Not Found."
        }

        val response = GetIOUResponse(results.map {
            IOUData(
                it.amount,
                it.lender.toString(),
                it.borrower.toString(),
                it.paid,
                it.linearId.toString()
            )
        })

        log.info("GetIOUByCreatedTimeFlow completed")
        return jsonMarshallingService.format(response)
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "getiou-1",
    "flowClassName": "com.r3.developers.samples.obligation.workflows.GetIOUByCreatedTimeFlow",
    "requestBody": {
        "from": 1111111111,
        "to": 2222222222
    }
}
*/