import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice'
import asnReducer from '@stores/asnSlice'
import orderReducer from '@stores/orderSlice'


const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer,
        order: orderReducer
    }
});

export default store;