"use client";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import Link from "next/link";

interface UserInterface {
  name: string;
  isLoggedIn: boolean;
}

export default function Header() {
  const [user, setUser] = useState<UserInterface | null>(null);

  useEffect(() => {
    // 로그인 상태 확인 (JSON 파싱 에러 방지)
    const userData = sessionStorage.getItem("user");
    if (userData && userData !== "undefined") {
      try {
        setUser(JSON.parse(userData));
      } catch (error) {
        console.error("Header - sessionStorage 데이터 파싱 오류:", error);
        sessionStorage.removeItem("user"); // 잘못된 데이터 제거
      }
    }
  }, []);

  return (
    <header className="bg-white border-b border-gray-200">
      {/* Top Navigation Bar */}
      <div className="bg-gray-50 border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between h-10">
            <div className="flex items-center space-x-6 text-sm">
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                기업보험
              </a>
              <a href="#" className="text-blue-600 font-medium">
                삼성화재 다이렉트
              </a>
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                회사소개
              </a>
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                IR
              </a>
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                공시실
              </a>
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                소비자포털
              </a>
              <a
                href="#"
                className="text-gray-600 hover:text-blue-600 transition-colors"
              >
                RC(보험설계사)
              </a>
            </div>
            <div className="flex items-center space-x-4 text-sm">
              {/* 로그인 상태에 따라 조건부 렌더링 */}
              {!user && (
                <>
                  <a
                    href="#"
                    className="text-gray-600 hover:text-blue-600 transition-colors"
                  >
                    회원 가입
                  </a>
                  <a
                    href="/login"
                    className="text-gray-600 hover:text-blue-600 transition-colors"
                  >
                    로그인
                  </a>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Main Header */}
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-20">
          {/* Logo */}
          <div className="flex items-center">
            <Link href="/">
              <h1 className="text-3xl font-bold text-blue-600 cursor-pointer hover:text-blue-700 transition-colors">
                삼성화재
              </h1>
            </Link>
          </div>

          {/* Navigation Menu */}
          <nav className="hidden md:flex items-center space-x-8">
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              개인관리
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              보험상품
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              보상
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              대출
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              퇴직연금
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              혜택/서비스
            </a>
            <a
              href="#"
              className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
            >
              고객센터
            </a>
          </nav>

          {/* Right Icons */}
          <div className="flex items-center space-x-4">
            {/* User Icon */}
            <div className="w-6 h-6 text-gray-600 cursor-pointer hover:text-blue-600 transition-colors" />
            <Search className="w-6 h-6 text-gray-600 cursor-pointer hover:text-blue-600 transition-colors" />
          </div>
        </div>
      </div>
    </header>
  );
}
