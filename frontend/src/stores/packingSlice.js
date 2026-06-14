import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { POST, PUT } from "@utils/Network";

const initialState = {
  loading: false,
  error: null,
  isModal: false,
  detailData: null,
  view: {
    summary: { total: 0, newpacking: 0, onpacking: 0, completed: 0 },
    list: [],
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 20
  },
};

export const getPacking = createAsyncThunk(
  'packing/list',
  async (credentials, { rejectWithValue }) => {
    try {
      return await POST('/packing', credentials);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getPackingDetail = createAsyncThunk(
  'packing/detail',
  async (credentials, { rejectWithValue }) => {
    try {
      return await POST(`/packing/${credentials.orderId}`);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const addPackingInvoice = createAsyncThunk(
  'packing/add',
  async (credentials, { rejectWithValue }) => {
    try {
      return await PUT('/packing', credentials);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const packingAsyncActions = [getPacking, getPackingDetail, addPackingInvoice];

const packingSlice = createSlice({
  name: 'packing',
  initialState,
  reducers: {
    openPackingModal: (state) => {
      state.isModal = true;
      state.detailData = null;
    },
    closePackingModal: (state) => {
      state.isModal = false;
      state.detailData = null;
    },
    setPage: (state, action) => {
      state.view.page = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(getPacking.fulfilled, (state, action) => {
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
      .addCase(getPackingDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;
          state.isModal = true;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      .addCase(addPackingInvoice.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          alert(res.message);
          state.isModal = false;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      });

    builder
      .addMatcher(
        isPending(...packingAsyncActions),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        isRejected(...packingAsyncActions),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || action.error.message || '알 수 없는 에러가 발생했습니다.';
        }
      );
  }
});

export const { openPackingModal, closePackingModal, setPage } = packingSlice.actions;
export default packingSlice.reducer;