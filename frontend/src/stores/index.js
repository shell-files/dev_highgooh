import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice'
import asnReducer from '@stores/asnSlice'
import orderReducer from '@stores/orderSlice'
import outboundHistoryReducer from '@stores/outboundHistorySlice'
import packingReducer from '@stores/packingSlice'


const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer,
        order: orderReducer,
        outboundHistory: outboundHistoryReducer,
        packing: packingReducer
    }
});

export default store;