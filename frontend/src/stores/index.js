import { configureStore } from "@reduxjs/toolkit";
import authReducer from '@stores/authSlice'
import asnReducer from '@stores/asnSlice'

const store = configureStore({
    reducer:{
        auth: authReducer,
        asn: asnReducer
    }
});

export default store;