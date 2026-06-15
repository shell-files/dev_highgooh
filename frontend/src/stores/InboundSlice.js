import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST, PUT, PATCH, DELETE } from "@utils/Network";
import { showDefaultAlert } from "@components/UI/ServiceAlert";

const initialState = {
  loading: false,
  error: null,
  isModal: false,
  detailData: null,
  view: {
    list: [],
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 20
  }
};

export const getInbound = createAsyncThunk(
  'inbound/list',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await POST('/inbound', credentials);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getInboundDetail = createAsyncThunk(
  'inbound/detail',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await POST(`/inbound/${id}`);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const inboundAsyncActions = [getInbound, getInboundDetail];

const inboundSlice = createSlice({
  name: 'inbound',
  initialState,
  reducers: {
    closeInboundModal: (state, action) => {
      state.isModal = false;
    },
    setPage: (state, action) => {
      state.view.page = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(getInbound.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.view.list = res.data.list;
          state.view.page = res.data.pagination.page;
          state.view.totalCount = res.data.pagination.totalCount;
          state.view.totalPages = res.data.pagination.totalPages;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      .addCase(getInboundDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;
          state.isModal = true;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      });

    builder
      .addMatcher(
        isPending(...inboundAsyncActions),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        isRejected(...inboundAsyncActions),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || action.error.message || '알 수 없는 에러가 발생했습니다.';
        }
      );
  }
});

export const { closeInboundModal, setPage } = inboundSlice.actions;
export default inboundSlice.reducer;