import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice'
import asnReducer from '@stores/asnSlice'
import orderReducer from '@stores/orderSlice'
import outboundHistoryReducer from '@stores/outboundHistorySlice'


const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer,
        order: orderReducer,
        outboundHistory: outboundHistoryReducer
    }
});

export default store;