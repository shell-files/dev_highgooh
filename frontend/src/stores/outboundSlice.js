import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST, PUT } from "@utils/Network";

const initialState = {
  loading: false,
  error: null,
  isModal: false,
  modalMode: 'register', // 'register' | 'detail' | 'vehicle' | 'invoice'
  detailData: null,

  view: {
    summary: {
      expectedToday: 0,
      confirmedToday: 0,
      nearDeadline: 0,
      overdue: 0,
      unassigned: 0
    },
    boxList: [],       // 박스 탭 목록 데이터
    manifestList: [],  // 매니페스트 탭 목록 데이터
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 20           // Outbound.jsx 기본 설정 규격인 20개 매핑
  },

  modal: {
    orders: [],
    vehicles: [],
    warehouses: []
  }
};

// ─────────────────────────────────────
// 1. 비동기 Thunk 액션 정의 (클로드 피드백 API 엔드포인트 규격 반영)
// ─────────────────────────────────────

// 박스 / 매니페스트 목록 조회
export const getOutboundList = createAsyncThunk(
  'outbound/list',
  async (credentials, { rejectWithValue }) => {
    try {
      // credentials에 viewMode('box'|'manifest') 및 필터 조건 포함하여 전송
      return await POST('/outbound', credentials);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 매니페스트 상세 (또는 박스 상세) 단건 조회
export const getOutboundDetail = createAsyncThunk(
  'outbound/detail',
  async (outboundId, { rejectWithValue }) => {
    try {
      return await POST(`/outbound/${outboundId}`);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 차량 배정 승인 요청 (OB-FIX-06 반영: PUT /outbound/vehicle)
export const assignOutboundVehicle = createAsyncThunk(
  'outbound/assignVehicle',
  async (vehicleData, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/vehicle', vehicleData);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 송장 발급 요청 (OB-FIX-07 반영: PUT /outbound/invoice)
export const issueOutboundInvoice = createAsyncThunk(
  'outbound/issueInvoice',
  async (invoiceData, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/invoice', invoiceData);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 출고 확정 요청 (OB-FIX-08 반영: PUT /outbound/confirm)
export const confirmOutboundShipment = createAsyncThunk(
  'outbound/confirmShipment',
  async (confirmData, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/confirm', confirmData);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const outboundAsyncActions = [
  getOutboundList,
  getOutboundDetail,
  assignOutboundVehicle,
  issueOutboundInvoice,
  confirmOutboundShipment
];

// ─────────────────────────────────────
// 2. 슬라이스 생성
// ─────────────────────────────────────
const outboundSlice = createSlice({
  name: 'outbound',
  initialState,
  reducers: {
    setOutboundPage: (state, action) => {
      state.view.page = action.payload;
    },
    clearOutboundError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // 목록 조회 성공 시 (박스 / 매니페스트 데이터 분기 수용)
      .addCase(getOutboundList.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.view.summary = res.data.summary || state.view.summary;
          
          // 백엔드가 현재 탭 세션에 맞춰 list를 유연하게 제공한다고 가정하고 바인딩
          if (res.data.viewMode === 'manifest') {
            state.view.manifestList = res.data.list || [];
          } else {
            state.view.boxList = res.data.list || [];
          }
          
          state.view.page = res.data.pagination?.page || 1;
          state.view.totalCount = res.data.pagination?.totalCount || 0;
          state.view.totalPages = res.data.pagination?.totalPages || 1;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 상세 조회 성공 시
      .addCase(getOutboundDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 차량 배정 완료 시
      .addCase(assignOutboundVehicle.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          alert('차량 배정이 완료되었습니다.');
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 송장 발급 완료 시
      .addCase(issueOutboundInvoice.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          alert("선택된 건들의 송장 인쇄 및 발급 공정이 마감되었습니다.");
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 출고 확정 완료 시
      .addCase(confirmOutboundShipment.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          alert('출고 처리가 최종 확정되었습니다.');
        } else {
          state.error = res.message;
        }
        state.loading = false;
      });

    // 강사님 스타일 공통 로딩/에러 매처 익스텐션
    builder
      .addMatcher(isPending(...outboundAsyncActions), (state) => {
        state.loading = true;
        state.error = null;
      })
      .addMatcher(isRejected(...outboundAsyncActions), (state, action) => {
        state.loading = false;
        state.error = action.payload || action.error.message || '알 수 없는 에러가 발생했습니다.';
      });
  }
});

export const { setOutboundPage, clearOutboundError } = outboundSlice.actions;
export default outboundSlice.reducer;