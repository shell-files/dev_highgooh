import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST, PUT, PATCH, DELETE } from "@utils/Network";
import { encodeJson, safeJsonParse } from "@utils/Base64";
import { showDefaultAlert } from "@components/UI/ServiceAlert";

const initialState = {
  loading: false,
  error: null,
  isModal: false,
  modalMode: 'register',
  detailData: null,
  view: {
    summary: { completed: 0, expected: 0, total: 0 },
    list: [],
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 20
  },
  modal: {
    partnerCompany: [],
    warehouses: [],
    materials: []
  }
};

export const getAsn = createAsyncThunk(
  'asn/list',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await POST('/asn', credentials);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getAsnDetail = createAsyncThunk(
  'asn/detail',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await POST(`/asn/${credentials.id}`);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getAsnModal = createAsyncThunk(
  'asn/modal',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await GET('/asn');
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const addAsnModal = createAsyncThunk(
  'asn/add',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await PUT('/asn', credentials);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const asnAsyncActions = [getAsn, getAsnDetail, getAsnModal, addAsnModal];

const asnSlice = createSlice({
  name: 'asn',
  initialState,
  reducers: {
    openAsnModal: (state, action) => {
      state.modalMode = 'register';
      state.detailData = null;
      state.isModal = true;
    },
    closeAsnModal: (state, action) => {
      state.isModal = false;
    },
    setPage: (state, action) => {
      state.view.page = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(getAsn.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.view.summary = res.data.summary;
          state.view.list = res.data.list;
          state.view.page = res.data.pagination.page;
          state.view.totalCount = res.data.pagination.totalCount;
          state.view.totalPages = res.data.pagination.totalPages;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      .addCase(getAsnDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;
          state.isModal = true;
          state.modalMode = 'detail';
        }else {
          state.error = res.message;
        }
        state.loading = false;
      })
      .addCase(getAsnModal.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.modal.partnerCompany = res.data.suppliers;
          state.modal.warehouses= res.data.warehouses;
          state.modal.materials = res.data.materials;
        }else {
          state.error = res.message;
        }
        state.loading = false;
      })
      .addCase(addAsnModal.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          // alert("사전입고 통지(ASN)가 등록되었습니다.");
          showDefaultAlert("등록 완료", "사전입고 통지(ASN)가 등록되었습니다.", "success");
          state.isModal = false;
        }else {
          state.error = res.message;
        }
        state.loading = false;
      });

    builder
      .addMatcher(
        isPending(...asnAsyncActions), 
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        isRejected(...asnAsyncActions), 
        (state, action) => {
          state.loading = false;
          state.error = action.payload || action.error.message || '알 수 없는 에러가 발생했습니다.';
        }
      );
  }
});

export const { openAsnModal, closeAsnModal, setPage } = asnSlice.actions;
export default asnSlice.reducer;