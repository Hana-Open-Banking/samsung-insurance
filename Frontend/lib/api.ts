import axios from "axios";

const BASE_URL = "https://7d39-112-76-112-180.ngrok-free.app";

// OAuth 클라이언트 정보
const CLIENT_ID = "CLIENT001";
const CLIENT_SECRET = "secret123";

interface TokenResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  refresh_token: string;
  scope: string;
}

interface User {
  userSeqNo: string;
  userName: string;
  gender: string;
  phoneNumber: string;
  userEmail: string;
  userInfo: string;
  createdAt: string;
}

interface InsuranceProduct {
  productId: string;
  productType: string;
  productName: string;
  totalPremium: number;
}

interface InsuranceContract {
  insuId: string;
  insuNum: string;
  productId: string;
  productName: string;
  insuType: string;
  insuStatus: string;
  issueDate: string;
  expDate: string;
  premium: number;
  paidPremium: number;
  specialYn: string;
  createdAt: string;
}

// 토큰 관리
class TokenManager {
  private static instance: TokenManager;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private tokenExpiry: number | null = null;

  private constructor() {
    // sessionStorage에서 토큰 복원
    if (typeof window !== "undefined") {
      this.accessToken = sessionStorage.getItem("access_token");
      this.refreshToken = sessionStorage.getItem("refresh_token");
      const expiry = sessionStorage.getItem("token_expiry");
      this.tokenExpiry = expiry ? parseInt(expiry) : null;
    }
  }

  public static getInstance(): TokenManager {
    if (!TokenManager.instance) {
      TokenManager.instance = new TokenManager();
    }
    return TokenManager.instance;
  }

  private saveTokens(tokenData: TokenResponse) {
    this.accessToken = tokenData.access_token;
    this.refreshToken = tokenData.refresh_token;
    this.tokenExpiry = Date.now() + tokenData.expires_in * 1000;

    if (typeof window !== "undefined") {
      sessionStorage.setItem("access_token", this.accessToken);
      sessionStorage.setItem("refresh_token", this.refreshToken);
      sessionStorage.setItem("token_expiry", this.tokenExpiry.toString());
    }
  }

  private clearTokens() {
    this.accessToken = null;
    this.refreshToken = null;
    this.tokenExpiry = null;

    if (typeof window !== "undefined") {
      sessionStorage.removeItem("access_token");
      sessionStorage.removeItem("refresh_token");
      sessionStorage.removeItem("token_expiry");
    }
  }

  private isTokenExpired(): boolean {
    if (!this.tokenExpiry) return true;
    return Date.now() >= this.tokenExpiry - 60000; // 1분 여유
  }

  public async getValidToken(): Promise<string | null> {
    if (this.accessToken && !this.isTokenExpired()) {
      return this.accessToken;
    }

    if (this.refreshToken) {
      try {
        await this.refreshAccessToken();
        return this.accessToken;
      } catch (error) {
        console.error("Token refresh failed:", error);
        this.clearTokens();
      }
    }

    try {
      await this.getNewToken();
      return this.accessToken;
    } catch (error) {
      console.error("Failed to get new token:", error);
      return null;
    }
  }

  private async getNewToken(): Promise<void> {
    const credentials = btoa(`${CLIENT_ID}:${CLIENT_SECRET}`);
    const params = new URLSearchParams();
    params.append("grant_type", "client_credentials");
    params.append("scope", "read");

    const response = await axios.post(`${BASE_URL}/oauth/token`, params, {
      headers: {
        Authorization: `Basic ${credentials}`,
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });

    this.saveTokens(response.data);
  }

  private async refreshAccessToken(): Promise<void> {
    if (!this.refreshToken) throw new Error("No refresh token available");

    const params = new URLSearchParams();
    params.append("grant_type", "refresh_token");
    params.append("refresh_token", this.refreshToken);

    const response = await axios.post(`${BASE_URL}/oauth/token`, params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });

    this.saveTokens(response.data);
  }
}

// API 호출 함수들
const tokenManager = TokenManager.getInstance();

async function apiRequest<T>(endpoint: string, options: any = {}): Promise<T> {
  const token = await tokenManager.getValidToken();
  if (!token) {
    throw new Error("Authentication failed");
  }

  const response = await axios({
    url: `${BASE_URL}${endpoint}`,
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  return response.data;
}

// 사용자 관리 API
export const userAPI = {
  // 사용자 목록 조회
  async getUsers(): Promise<User[]> {
    return apiRequest<User[]>("/api/users");
  },

  // 사용자 상세 조회
  async getUser(userSeqNo: string): Promise<User> {
    return apiRequest<User>(`/api/users/${userSeqNo}`);
  },

  // 사용자 등록
  async registerUser(userData: any): Promise<User> {
    return apiRequest<User>("/api/users", {
      method: "POST",
      data: userData,
    });
  },
};

// 보험 상품 관리 API
export const productAPI = {
  // 보험 상품 목록 조회
  async getProducts(): Promise<InsuranceProduct[]> {
    return apiRequest<InsuranceProduct[]>("/api/products");
  },

  // 보험 상품 상세 조회
  async getProduct(productId: string): Promise<InsuranceProduct> {
    return apiRequest<InsuranceProduct>(`/api/products/${productId}`);
  },
};

// 보험 계약 관리 API
export const contractAPI = {
  // 사용자별 보험 계약 목록 조회
  async getUserContracts(userSeqNo: string): Promise<InsuranceContract[]> {
    return apiRequest<InsuranceContract[]>(`/api/users/${userSeqNo}/contracts`);
  },

  // 보험 계약 상세 조회
  async getContract(insuId: string): Promise<InsuranceContract> {
    return apiRequest<InsuranceContract>(`/api/contracts/${insuId}`);
  },
};

// 인증 상태 관리
export const authAPI = {
  // 토큰 유효성 확인
  async isAuthenticated(): Promise<boolean> {
    const token = await tokenManager.getValidToken();
    return !!token;
  },

  // 로그아웃
  logout() {
    tokenManager["clearTokens"]();
  },
};

// 로그인 및 사용자 정보 가져오기 (새로운 API 방식)
export async function loginAndGetUser() {
  try {
    // 1. 로그인 API 호출
    const loginResponse = await axios.post(`${BASE_URL}/api/auth/login`, {
      userEmail: "hong.gildong@example.com",
      password: "password123!@#",
    });

    const { accessToken, userSeqNo } = loginResponse.data;

    // 2. 내 정보 조회
    const userResponse = await axios.get(`${BASE_URL}/api/users/me`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    return {
      ...userResponse.data,
      name: userResponse.data.userName || "사용자",
      isLoggedIn: true,
      accessToken,
      userSeqNo,
    };
  } catch (error) {
    console.error("로그인 또는 사용자 정보 가져오기 실패:", error);
    return null;
  }
}
