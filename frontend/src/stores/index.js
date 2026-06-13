import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice'
import asnReducer from '@stores/asnSlice'
import outboundReducer from '@stores/outboundSlice';

const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer,
        outbound: outboundReducer,
    }
});

export default store;