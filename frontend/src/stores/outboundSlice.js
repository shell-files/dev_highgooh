import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST, PUT } from "@utils/Network"; // 기존 작성 인프라 사용

const initialState = {
  loading: false,
  error: null,
  isModal: false,
  modalMode: 'detail',
  detailData: null,

  carriers: [],
  vehicles: [],

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
    size: 10           // 기본 페이징 스펙 규격 동기화
  }
};

// ─────────────────────────────────────
// 1. 비동기 Thunk 액션 정의
// ─────────────────────────────────────

export const getOutboundFormData = createAsyncThunk(
  'outbound/formData',
  async (_, { rejectWithValue }) => {
    try {
      return await GET('/outbound');
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 박스 및 매니페스트 데이터 목록 동적 획득
export const getOutboundList = createAsyncThunk(
  'outbound/list',
  async (filters, { rejectWithValue }) => {
    try {
      return await POST('/outbound', filters);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 단건 출고 상세 보기 데이터 획득
export const getOutboundDetail = createAsyncThunk(
  'outbound/detail',
  async (outboundId, { rejectWithValue }) => {
    try {
      return await GET(`/outbound/detail/${outboundId}`);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 21번 차량 배정 확정 승인 공정
export const assignOutboundVehicle = createAsyncThunk(
  'outbound/assignVehicle',
  async (payload, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/vehicle', payload);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 22번 고유 송장 발급 및 출력 마감 승인 공정
export const issueOutboundInvoice = createAsyncThunk(
  'outbound/issueInvoice',
  async (payload, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/invoice', payload);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

// 23번 출고 최종 확정 공정 (매니페스트 기준 완료)
export const confirmOutboundShipment = createAsyncThunk(
  'outbound/confirmShipment',
  async (payload, { rejectWithValue }) => {
    try {
      return await PUT('/outbound/confirm', payload);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getOutboundManifestList = createAsyncThunk(
  'outbound/manifestList',
  async (filters, { rejectWithValue }) => {
    try {
      console.log(await POST('/outbound/manifest', filters))
      return await POST('/outbound/manifest', filters);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const outboundAsyncActions = [
  getOutboundFormData,
  getOutboundList,
  getOutboundManifestList,
  getOutboundDetail,
  assignOutboundVehicle,
  issueOutboundInvoice,
  confirmOutboundShipment
];

// ─────────────────────────────────────
// 2. 슬라이스 본체 및 풀필드 매퍼 설계
// ─────────────────────────────────────
const outboundSlice = createSlice({
  name: 'outbound',
  initialState,
  reducers: {
    setOutboundPage: (state, action) => {
      state.view.page = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(getOutboundFormData.fulfilled, (state, action) => {
        // console.log("폼 데이터", action.payload);

        const res = action.payload;

        if (res && res.status === true) {
          state.carriers = res.data?.carriers || [];
          state.vehicles = res.data?.vehicles || [];
        } else {
          state.error = res?.message || "폼 데이터 조회 실패";
        }

        state.loading = false;
      })

      .addCase(getOutboundManifestList.fulfilled, (state, action) => {
        const res = action.payload;

        if (res?.status) {
          state.view.manifestList = res.data?.list || [];

          state.view.page =
            res.data?.pagination?.page || 1;

          state.view.totalCount =
            res.data?.pagination?.totalCount || 0;

          state.view.totalPages =
            res.data?.pagination?.totalPages || 1;
        }

        state.loading = false;
      })

      // 1) 박스 / 매니페스트 목록 조회 성공 시
      .addCase(getOutboundList.fulfilled, (state, action) => {
        // 💡 F12 콘솔창에서 백엔드가 준 진짜 데이터의 형태를 확인하는 로그입니다.
        // console.log("폼 데이터", action.payload);

        const res = action.payload;

        if (res && res.status === true) {
          // 백엔드의 res.data.list 데이터를 boxList와 manifestList에 유연하게 매핑합니다.
          state.view.list = res.data?.list || [];
          state.view.boxList = res.data?.list || [];
          state.view.manifestList = res.data?.list || [];


          // 페이지네이션 규격 연동
          state.view.page = res.data?.pagination?.page || 1;
          state.view.totalCount = res.data?.pagination?.totalCount || 0;
          state.view.totalPages = res.data?.pagination?.totalPages || 1;

          // 상단 대시보드 카드 요약 데이터 연동
          if (res.data?.summary) {
            state.view.summary = res.data.summary;
          }
        } else {
          state.error = res?.message || "데이터 로드 실패";
        }
        state.loading = false;
      })

      // 2) 단건 상세조회 성공 시 바인딩
      .addCase(getOutboundDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res && res.status === true) {
          state.detailData = res.data; // 필요에 따라 상세 보기 팝업 데이터 연동용
        } else {
          state.error = res?.message || "상세 내역 조회 실패";
        }
        state.loading = false;
      })

      // 3) 차량 배정 피드백 알림
      .addCase(assignOutboundVehicle.fulfilled, (state, action) => {
        const res = action.payload;
        if (res && res.status === true) {
          alert('차량 배정이 성공적으로 처리되었습니다.');
        } else {
          alert(res?.message || '차량 배정 처리 중 오류가 발생했습니다.');
        }
        state.loading = false;
      })

      // 4) 송장 인쇄 공정 피드백 알림
      .addCase(issueOutboundInvoice.fulfilled, (state, action) => {
        const res = action.payload;
        if (res && res.status === true) {
          alert('선택된 박스들의 송장 마감 및 공정 채번이 완료되었습니다.');
        } else {
          alert(res?.message || '송장 처리 중 문제가 발생했습니다.');
        }
        state.loading = false;
      })

      // 5) 최종 출고 확정 피드백 알림
      .addCase(confirmOutboundShipment.fulfilled, (state, action) => {
        const res = action.payload;
        if (res && res.status === true) {
          alert('선택한 매니페스트 단위 출고가 최종 확정 마감되었습니다.');
        } else {
          alert(res?.message || '출고 확정 공정 처리 실패');
        }
        state.loading = false;
      });

    // 로딩 및 에러 통신 처리 공통 매처
    builder
      .addMatcher(isPending(...outboundAsyncActions), (state) => {
        state.loading = true;
        state.error = null;
      })
      .addMatcher(isRejected(...outboundAsyncActions), (state, action) => {
        state.loading = false;
        state.error = action.payload?.message || "서버 통신 중 장애 발생";
      });
  }
});

export const { setOutboundPage } = outboundSlice.actions;
export default outboundSlice.reducer;