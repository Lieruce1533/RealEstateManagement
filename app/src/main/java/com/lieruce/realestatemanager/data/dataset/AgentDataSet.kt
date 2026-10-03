package com.lieruce.realestatemanager.data.dataset

import com.lieruce.realestatemanager.data.model.Agent

/**
 * DataSet providing initial real estate agents for database seeding.
 */
object AgentDataSet {
    fun getInitialAgents(): List<Agent> {
        return listOf(
            Agent(id = 1L, name = "Agent Smith", email = "smith@realestate.com", phone = "+1 (555) 019-2834"),
            Agent(id = 2L, name = "Agent Jane", email = "jane@realestate.com", phone = "+1 (555) 839-2041"),
            Agent(id = 3L, name = "Agent Dupont", email = "dupont@realestate.com", phone = "+1 (555) 492-1093")
        )
    }
}
