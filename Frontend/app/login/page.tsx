"use client";

import type React from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Eye, EyeOff, Lock, User } from "lucide-react";
import axios from "axios";

export default function LoginPage() {
  const router = useRouter();
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    rememberMe: false,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        "https://7d39-112-76-112-180.ngrok-free.app/api/auth/login",
        {
          userEmail: "hong.gildong@example.com",
          password: "password123!@#",
        },
        {
          headers: {
            "ngrok-skip-browser-warning": "true", // ngrok 경고 페이지 우회
          },
        }
      );

      const { accessToken, userSeqNo } = response.data;

      // 2. 내 정보 조회 (ngrok 경고 페이지 우회)
      const userResponse = await axios.get(
        "https://7d39-112-76-112-180.ngrok-free.app/api/users/me",
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "ngrok-skip-browser-warning": "true", // ngrok 경고 페이지 우회
          },
        }
      );

      console.log("✅ 로그인 성공! accessToken:", accessToken);
      console.log("✅ 사용자 정보 응답:", userResponse.data);

      // 3. 사용자 정보 저장
      const userData = {
        name: userResponse.data.userName || "사용자",
        isLoggedIn: true,
        userSeqNo: userSeqNo,
        accessToken: accessToken,
      };

      console.log("💾 sessionStorage에 저장할 데이터:", userData);
      sessionStorage.setItem("user", JSON.stringify(userData));

      router.push("/");
    } catch (error) {
      console.error("로그인 실패:", error);
      alert("로그인에 실패했습니다.");
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <Card className="shadow-lg">
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl text-center text-blue-600">
              로그인
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-4">
                <div>
                  <Label
                    htmlFor="username"
                    className="text-sm font-medium text-gray-700"
                  >
                    아이디
                  </Label>
                  <div className="mt-1 relative">
                    <Input
                      id="username"
                      name="username"
                      type="text"
                      required
                      value={formData.username}
                      onChange={handleInputChange}
                      className="pl-10"
                      placeholder="아이디를 입력하세요"
                    />
                    <User className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  </div>
                </div>

                <div>
                  <Label
                    htmlFor="password"
                    className="text-sm font-medium text-gray-700"
                  >
                    비밀번호
                  </Label>
                  <div className="mt-1 relative">
                    <Input
                      id="password"
                      name="password"
                      type={showPassword ? "text" : "password"}
                      required
                      value={formData.password}
                      onChange={handleInputChange}
                      className="pl-10 pr-10"
                      placeholder="비밀번호를 입력하세요"
                    />
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    >
                      {showPassword ? (
                        <EyeOff className="h-4 w-4" />
                      ) : (
                        <Eye className="h-4 w-4" />
                      )}
                    </button>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="rememberMe"
                    checked={formData.rememberMe}
                    onCheckedChange={(checked) =>
                      setFormData((prev) => ({
                        ...prev,
                        rememberMe: checked as boolean,
                      }))
                    }
                  />
                  <Label htmlFor="rememberMe" className="text-sm text-gray-600">
                    로그인 상태 유지
                  </Label>
                </div>
                <div className="text-sm">
                  <span className="text-blue-600 hover:text-blue-500 cursor-pointer">
                    아이디/비밀번호 찾기
                  </span>
                </div>
              </div>

              <Button
                type="submit"
                className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 text-lg font-medium"
              >
                로그인
              </Button>
            </form>

            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300" />
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">또는</span>
                </div>
              </div>

              <div className="mt-6 grid grid-cols-2 gap-3">
                <Button variant="outline" className="w-full bg-transparent">
                  <span className="text-sm">간편 로그인</span>
                </Button>
                <Button variant="outline" className="w-full bg-transparent">
                  <span className="text-sm">생체 인증</span>
                </Button>
              </div>
            </div>

            <div className="mt-6 text-center">
              <p className="text-sm text-gray-600">
                아직 계정이 없으신가요?{" "}
                <span className="text-blue-600 hover:text-blue-500 cursor-pointer font-medium">
                  회원가입
                </span>
              </p>
            </div>
          </CardContent>
        </Card>

        <div className="mt-6 text-center text-xs text-gray-500">
          <p>© 2024 삼성화재해상보험주식회사. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
}
