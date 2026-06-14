import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST } from "@utils/Network";

const initialState = {
  loading: false,
  detailLoading: false,
  error: null,
  isModal: false,
  detailData: null,
  view: {
    summary: { total: 0, achieved: 0, overdue: 0 },
    list: [],
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 10
  },
  // 💡 초기 진입 시 구조가 깨지지 않도록 확실하게 기본 객체 선언
  filters: {
    customers: [],
    transports: []
  }
};

// 1. 대시보드 통계 및 리스트 조회 
export const getOutboundHistory = createAsyncThunk(
  'outboundHistory/list',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await POST('/outboundHistory', credentials);
      // console.log('📥 response:', response);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 2. 단건 상세 조회용 
export const getOutboundHistoryDetail = createAsyncThunk(
  'outboundHistory/detail',
  async (outboundId, { rejectWithValue }) => {
    try {
      const response = await POST(`/outboundHistory/${outboundId}`);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 3. 파트너사 리스트 동적 조회 Thunk
export const getPartnerList = createAsyncThunk(
  'outboundHistory/partners',
  async (_, { rejectWithValue }) => {
    try {
      const response = await GET('/outboundHistory/partners');
      // console.log("파트너:", response)
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const outboundHistoryAsyncActions = [getOutboundHistory, getPartnerList];

const outboundHistorySlice = createSlice({
  name: 'outboundHistory',
  initialState,
  reducers: {
    setPage: (state, action) => {
      state.view.page = action.payload;
    },
    openModal: (state) => {
      state.isModal = true;
    },
    closeModal: (state) => {
      state.isModal = false;
      state.detailData = null;
    }
  },
  // 1. 상단 배열에서 getOutboundHistoryDetail 제거

  extraReducers: (builder) => {
    builder
      .addCase(getOutboundHistory.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true && res.data) {
          state.view.summary = {
            total: res.data.totalCompletedCount || 0,
            achieved: res.data.onTimeCompletedCount || 0,
            overdue: res.data.delayedCompletedCount || 0
          };
          state.view.list = res.data.list || [];
          state.view.totalCount = res.data.totalCount || 0;
          state.view.totalPages = res.data.totalPages || 1;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })

      // 2. detail은 detailLoading으로 따로 관리
      .addCase(getOutboundHistoryDetail.pending, (state) => {
        state.detailLoading = true;  // ← loading 건드리지 않음
      })
      .addCase(getOutboundHistoryDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;
          state.isModal = true;
        } else {
          state.error = res.message;
        }
        state.detailLoading = false;  // ← detailLoading만
      })
      .addCase(getOutboundHistoryDetail.rejected, (state, action) => {
        state.error = action.payload?.message || "상세 조회 중 오류가 발생했습니다.";
        state.detailLoading = false;
      })

      .addCase(getPartnerList.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true && Array.isArray(res.data)) {
          state.filters.customers = res.data.filter(item => item.customer_yn_code === 1) || [];  // 'Y' → 1
          state.filters.transports = res.data.filter(item => item.carrier_yn_code === 1) || [];  // 'Y' → 1
        } else if (res.status === true && res.data && Array.isArray(res.data.list)) {
          state.filters.customers = res.data.list.filter(item => item.customer_yn_code === 1) || [];
          state.filters.transports = res.data.list.filter(item => item.carrier_yn_code === 1) || [];
        } else {
          state.error = res.message;
        }
        state.loading = false;
      });

    builder
      .addMatcher(
        isPending(...outboundHistoryAsyncActions),  // detail 빠졌으니 list/partners만 잡음
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        isRejected(...outboundHistoryAsyncActions),
        (state, action) => {
          state.loading = false;
          state.error = action.payload?.message || "서버 통신 중 오류가 발생했습니다.";
        }
      );
  }
});

export const { setPage, openModal, closeModal } = outboundHistorySlice.actions;
export default outboundHistorySlice.reducer;