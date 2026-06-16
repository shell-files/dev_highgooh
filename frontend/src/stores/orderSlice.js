import { createSlice, createAsyncThunk, isPending, isRejected } from '@reduxjs/toolkit';
import { GET, POST, PUT, PATCH } from "@utils/Network";
import { showDefaultAlert } from "@components/UI/ServiceAlert";

// ─────────────────────────────────────────────────────────────
// initialState
// ─────────────────────────────────────────────────────────────
const initialState = {
  loading: false,
  error: null,
  isModal: false,
  modalMode: 'register',     // 'register' | 'detail' | 'add'
  detailData: null,          // { order: {...}, items: [...] }
  view: {
    summary: { total: 0, newOrder: 0, inProgress: 0, completed: 0 },
    list: [],
    page: 1,
    totalCount: 0,
    totalPages: 0,
    size: 10
  },
  modal: {
    customers: [],           // GET /order → data.customers
    products: []            // GET /order → data.products
  }
};

// ─────────────────────────────────────────────────────────────
// Thunk 비동기 액션 정의
// ─────────────────────────────────────────────────────────────
export const getOrder = createAsyncThunk(
  'order/list',
  async (credentials, { rejectWithValue }) => {
    try {
      return await POST('/order', credentials);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getOrderDetail = createAsyncThunk(
  'order/detail',
  async (credentials, { rejectWithValue }) => {
    try {
      return await POST(`/order/${credentials.outboundId}`);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const getOrderModal = createAsyncThunk(
  'order/modal',
  async (_, { rejectWithValue }) => {
    try {
      return await GET('/order');
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const addOrder = createAsyncThunk(
  'order/add',
  async (credentials, { rejectWithValue }) => {
    try {
      return await PUT('/order', credentials);
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

export const completeOrder = createAsyncThunk(
  'order/complete',
  async (credentials, { rejectWithValue }) => {
    try {
      return await PATCH(`/order/${credentials.outboundId}`, { etd: credentials.etd });
    } catch (error) {
      return rejectWithValue(error.response?.data);
    }
  }
);

const orderAsyncActions = [getOrder, getOrderDetail, getOrderModal, addOrder, completeOrder];

// ─────────────────────────────────────────────────────────────
// Slice 정의
// ─────────────────────────────────────────────────────────────
const orderSlice = createSlice({
  name: 'order',
  initialState,

  // 동기적 액션 처리 reducers
  reducers: {
    // ── orderSlice.js 내의 reducers 옵션 중 openOrderModal을 아래처럼 수정 ──
    openOrderModal: (state, action) => {
      state.isModal = true;

      // 💡 만약 메인 화면에서 인자 없이 dispatch(openOrderModal())을 호출하더라도 
      // 에러가 나지 않고 기본값 'add'로 지정되도록 안전장치를 둡니다.
      const mode = action.payload?.mode || 'add';
      state.modalMode = mode;

      if (mode === 'add') {
        // 신규 등록일 때는 이전 상세조회 데이터 잔상을 완전히 박멸합니다.
        state.detailData = null;
      } else if (mode === 'detail') {
        state.detailData = action.payload?.data || null;
      }
    },
    closeOrderModal: (state) => {
      state.isModal = false;
      state.detailData = null; // 닫을 때도 깔끔하게 비우기
    },
    // 하단 export에 명시되어 있던 페이지 변경 리듀서 추가 안전장치
    setOrderPage: (state, action) => {
      state.view.page = action.payload;
    }
  }, // 👈 여기서 닫지 않고 쉼표(,)로 이어서 extraReducers를 포함시킵니다!

  // 비동기적 액션 처리 extraReducers
  extraReducers: (builder) => {
    builder
      // 목록 조회 성공
      .addCase(getOrder.fulfilled, (state, action) => {
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
      // 상세 조회 성공 — detailData 저장 후 detail 모달 열기
      .addCase(getOrderDetail.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.detailData = res.data;   // { order: {...}, items: [...] }
          state.isModal = true;
          state.modalMode = 'detail';
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 기초 데이터 조회 성공 — customers, products 저장
      .addCase(getOrderModal.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          state.modal.customers = res.data.customers;
          state.modal.products = res.data.products;
        } else {
          state.error = res.message;
        }
        state.loading = false;
      })
      // 등록 성공 — 모달 닫기
      .addCase(addOrder.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          showDefaultAlert('등록 완료', res.message, 'success');
          state.isModal = false;
        } else {
          state.error = res.message;
          showDefaultAlert('오류', res.message || '주문 등록에 실패했습니다.', 'error');
        }
        state.loading = false;
      })
      // 처리중으로 변경
      .addCase(completeOrder.fulfilled, (state, action) => {
        const res = action.payload;
        if (res.status === true) {
          showDefaultAlert("완료", res.message, "success");
          state.isModal = false;
        } else {
          showDefaultAlert("오류", res.message, "error");
        }
        state.loading = false;
      });

  // loading / error 공통 처리
  builder
      .addMatcher(
    isPending(...orderAsyncActions),
    (state) => {
      state.loading = true;
      state.error = null;
    }
  )
    .addMatcher(
      isRejected(...orderAsyncActions),
      (state, action) => {
        state.loading = false;
        state.error = action.payload || action.error.message || '알 수 없는 에러가 발생했습니다.';
      }
    );
}
});

export const { openOrderModal, closeOrderModal, setOrderPage } = orderSlice.actions;
export default orderSlice.reducer;