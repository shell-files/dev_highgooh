import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice';
import asnReducer from '@stores/asnSlice';
import inboundReducer from '@stores/inboundSlice';
import orderReducer from '@stores/orderSlice';
import packingReducer from '@stores/packingSlice';
import outboundReducer from '@stores/outboundSlice';
import outboundHistoryReducer from '@stores/outboundHistorySlice';

const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer,
        inbound: inboundReducer,
        order: orderReducer,
        packing: packingReducer,
        outbound: outboundReducer,
        outboundHistory: outboundHistoryReducer
    }
});

export default store;