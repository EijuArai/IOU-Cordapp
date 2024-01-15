package com.r3.developers.samples.obligation.query

import com.r3.developers.samples.obligation.states.IOUState
import net.corda.v5.ledger.utxo.StateAndRef
import net.corda.v5.ledger.utxo.query.VaultNamedQueryFactory
import net.corda.v5.ledger.utxo.query.VaultNamedQueryStateAndRefTransformer
import net.corda.v5.ledger.utxo.query.registration.VaultNamedQueryBuilderFactory

class IOUStateQueryFactory : VaultNamedQueryFactory {
    override fun create(vaultNamedQueryBuilderFactory: VaultNamedQueryBuilderFactory) {
        vaultNamedQueryBuilderFactory.create("GET_BY_CREATED_TIME")
            .whereJson(
                "WHERE visible_states.custom_representation -> 'com.r3.developers.samples.obligation.states.IOUState' IS NOT NULL " +
                        "AND visible_states.created >= to_timestamp(:from) " +
                        "AND visible_states.created <= to_timestamp(:to) " +
                        "AND visible_states.consumed IS NULL"
            )
            .map(IOUStateQueryTransformer())
            .register()
    }
}

class IOUStateQueryTransformer :
    VaultNamedQueryStateAndRefTransformer<IOUState, IOUState> {
    override fun transform(data: StateAndRef<IOUState>, parameters: MutableMap<String, Any>): IOUState {
        return data.state.contractState
    }
}