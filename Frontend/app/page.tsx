"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { ChevronLeft, ChevronRight, Pause } from "lucide-react";
import Image from "next/image";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { loginAndGetUser } from "@/lib/api";

interface User {
  name: string;
  isLoggedIn: boolean;
}

export default function HomePage() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // sessionStorage에서 사용자 정보만 확인 (자동 로그인 X)
    const storedUser = sessionStorage.getItem("user");
    console.log("🏠 홈 페이지 - sessionStorage에서 읽은 데이터:", storedUser);

    if (storedUser && storedUser !== "undefined") {
      try {
        const parsedUser = JSON.parse(storedUser);
        console.log("🏠 홈 페이지 - 파싱된 사용자 데이터:", parsedUser);
        setUser(parsedUser);
      } catch (error) {
        console.error("sessionStorage 데이터 파싱 오류:", error);
        sessionStorage.removeItem("user"); // 잘못된 데이터 제거
      }
    }
    setLoading(false);
  }, []);

  const handleLogout = () => {
    sessionStorage.removeItem("user");
    setUser(null);
    // 페이지 새로고침으로 헤더도 업데이트
    window.location.reload();
  };

  const handleMyPage = () => {
    // 마이페이지로 이동
    router.push("/mypage");
  };

  return (
    <div className="min-h-screen bg-white">
      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Content */}
          <div className="lg:col-span-2">
            {/* Promotional Banner */}
            <div className="bg-gradient-to-r from-yellow-50 to-orange-50 rounded-lg p-8 mb-8 relative overflow-hidden">
              <div className="flex items-center justify-between">
                <div className="z-10">
                  <div className="text-orange-600 text-sm font-medium mb-2">
                    교통안전송 챌린지
                  </div>
                  <h2 className="text-3xl font-bold text-gray-800 mb-2 leading-tight">
                    카르르 챌린지 참여 시<br />
                    네이버페이 1만원
                  </h2>
                  <p className="text-gray-600 mb-6">참여자 전원 증정</p>
                  <Button variant="outline" className="bg-white cursor-pointer">
                    자세히보기
                  </Button>
                </div>
                <div className="flex-1 flex justify-center">
                  <Image
                    src="/images/kareuru-challenge.png"
                    alt="삼성화재 카르르 챌린지"
                    width={280}
                    height={210}
                    className="object-contain"
                  />
                </div>
              </div>

              {/* Banner Controls */}
              <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex items-center space-x-2 bg-black bg-opacity-50 rounded-full px-3 py-1">
                <ChevronLeft className="w-4 h-4 text-white cursor-pointer hover:text-gray-300 transition-colors" />
                <span className="text-white text-sm">6</span>
                <span className="text-white text-sm">/</span>
                <span className="text-white text-sm">6</span>
                <ChevronRight className="w-4 h-4 text-white cursor-pointer hover:text-gray-300 transition-colors" />
                <div className="w-px h-4 bg-white bg-opacity-50 mx-2"></div>
                <Pause className="w-4 h-4 text-white cursor-pointer hover:text-gray-300 transition-colors" />
              </div>
            </div>

            {/* Insurance Products Section */}
            <div className="mb-8">
              <h3 className="text-2xl font-bold text-gray-800 mb-6">
                많은 분들이 선택한 상품이에요
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                  <CardContent className="p-6">
                    <div className="flex items-start space-x-4">
                      <div className="w-24 h-24 flex items-center justify-center">
                        <Image
                          src="/images/다이렉트-해외여행보험.png"
                          alt="다이렉트 해외여행보험"
                          width={96}
                          height={96}
                          className="object-contain"
                        />
                      </div>
                      <div className="flex-1">
                        <h4 className="font-bold text-gray-800 mb-2">
                          다이렉트 해외여행보험
                        </h4>
                        <p className="text-gray-600 text-sm mb-1 leading-tight">
                          안전한
                        </p>
                        <p className="text-gray-600 text-sm leading-tight">
                          나의 여행을 위해
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                  <CardContent className="p-6">
                    <div className="flex items-start space-x-4">
                      <div className="w-24 h-24 flex items-center justify-center">
                        <Image
                          src="/images/다이렉트-자동차보험.png"
                          alt="다이렉트 자동차보험"
                          width={96}
                          height={96}
                          className="object-contain"
                        />
                      </div>
                      <div className="flex-1">
                        <h4 className="font-bold text-gray-800 mb-2">
                          다이렉트 자동차보험
                        </h4>
                        <p className="text-gray-600 text-sm mb-1 leading-tight">
                          다이렉트로 자동차
                        </p>
                        <p className="text-gray-600 text-sm leading-tight">
                          보험서비스는 그대로!
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            </div>

            {/* Additional Info */}
            <div className="bg-blue-50 rounded-lg p-6 cursor-pointer hover:bg-blue-100 transition-colors">
              <div className="flex items-center space-x-4">
                <div className="w-28 h-28 flex items-center justify-center">
                  <Image
                    src="/images/한눈에-보기.png"
                    alt="보험상품 한눈에 보기"
                    width={112}
                    height={112}
                    className="object-contain"
                  />
                </div>
                <div className="flex items-center space-x-2">
                  <h4 className="font-bold text-gray-800">
                    보험상품 한눈에 보기
                  </h4>
                  <ChevronRight className="w-5 h-5 text-gray-600" />
                </div>
              </div>
            </div>
          </div>

          {/* Right Sidebar */}
          <div className="space-y-6">
            {/* Login/User Section */}
            <Card>
              <CardContent className="p-6">
                {user ? (
                  // 로그인된 상태
                  <>
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-gray-800 font-bold">
                        {user.name}님, 환영합니다!
                      </span>
                    </div>
                    <Button
                      onClick={handleMyPage}
                      className="w-full bg-blue-600 hover:bg-blue-700 text-white mb-4 cursor-pointer"
                    >
                      내 보험 보러가기
                    </Button>
                    {/* 로그인 시 계약조회, 보험금청구 숨기고 로그아웃만 표시 */}
                    <div className="flex justify-end text-sm">
                      <button
                        onClick={handleLogout}
                        className="text-gray-600 hover:text-gray-700 transition-colors font-medium cursor-pointer"
                      >
                        로그아웃
                      </button>
                    </div>
                  </>
                ) : loading ? (
                  // 로딩 상태
                  <>
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-gray-600 font-bold">
                        로그인 중...
                      </span>
                    </div>
                    <div className="w-full bg-gray-200 h-10 rounded animate-pulse"></div>
                  </>
                ) : (
                  // 로그인되지 않은 상태
                  <>
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-gray-600 font-bold">
                        안녕하세요!
                      </span>
                    </div>
                    <a href="/login">
                      <Button className="w-full bg-blue-600 hover:bg-blue-700 text-white mb-4 cursor-pointer">
                        로그인
                      </Button>
                    </a>
                    <div className="flex justify-between text-sm text-gray-600">
                      <span className="cursor-pointer hover:text-blue-600 transition-colors">
                        회원 가입
                      </span>
                      <span className="cursor-pointer hover:text-blue-600 transition-colors">
                        아이디/비밀번호 찾기
                      </span>
                    </div>
                  </>
                )}

                <div className="mt-4 pt-4 border-t border-gray-200">
                  <p className="text-sm text-gray-600 mb-2">
                    삼성화재 카르르 챌린지
                  </p>
                  <p className="text-sm text-gray-600 mb-3 leading-tight">
                    참여만 해도 네이버페이 1만원을 드려요!
                  </p>
                  <Button
                    variant="outline"
                    size="sm"
                    className="w-full bg-transparent cursor-pointer"
                  >
                    자세히보기
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* Services Grid */}
            <Card>
              <CardContent className="p-6">
                <h4 className="font-bold text-gray-800 mb-4">자주찾는 메뉴</h4>
                <div className="grid grid-cols-3 gap-4">
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/개인대출확인.png"
                        alt="개인대출 확인"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      개인대출
                      <br />
                      확인
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/보험금청구.png"
                        alt="보험금 청구"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      보험금
                      <br />
                      청구
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/보험계약대출.png"
                        alt="보험계약 대출"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      보험계약
                      <br />
                      대출
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/보험료납입.png"
                        alt="보험료 납입"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      보험료
                      <br />
                      납입
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/증명서발급.png"
                        alt="증명서 발급"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      증명서
                      <br />
                      발급
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/온라인대모니터링.png"
                        alt="온라인대 모니터링"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      온라인대
                      <br />
                      모니터링
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/임시운전특약.png"
                        alt="임시운전 특약"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      임시운전
                      <br />
                      특약
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/필수서류안내.png"
                        alt="필수서류 안내"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      필수서류
                      <br />
                      안내
                    </p>
                  </div>
                  <div className="text-center cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors">
                    <div className="w-12 h-12 flex items-center justify-center mx-auto mb-2">
                      <Image
                        src="/images/자동차사고접수.png"
                        alt="자동차 사고접수"
                        width={48}
                        height={48}
                        className="object-contain"
                      />
                    </div>
                    <p className="text-xs text-gray-600 leading-tight">
                      자동차
                      <br />
                      사고접수
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
}
