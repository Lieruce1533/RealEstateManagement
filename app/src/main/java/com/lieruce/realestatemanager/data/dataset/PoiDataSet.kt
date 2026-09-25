package com.lieruce.realestatemanager.data.dataset

import com.lieruce.realestatemanager.data.model.Poi
import com.lieruce.realestatemanager.data.model.PropertyConstants

/**
 * DataSet providing standardized points of interest for database seeding matching PropertyConstants.AVAILABLE_POIS.
 */
object PoiDataSet {
    fun getInitialPois(): List<Poi> {
        return PropertyConstants.AVAILABLE_POIS.mapIndexed { index, name ->
            Poi(id = (index + 1).toLong(), name = name)
        }
    }
}
