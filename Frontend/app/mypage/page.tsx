"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ExternalLink } from "lucide-react"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"

interface User {
  name: string
  isLoggedIn: boolean
}

export default function MyPage() {
  const router = useRouter()
  const [user, setUser] = useState<User | null>(null)
  const [activeTab, setActiveTab] = useState("내 보험")

  useEffect(() => {
    // 로그인 상태 확인
    const userData = localStorage.getItem("user")
    if (userData) {
      setUser(JSON.parse(userData))
    } else {
      // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
      router.push("/login")
    }
  }, [router])

  if (!user) {
    return <div>Loading...</div>
  }

  const tabs = ["내 보험", "계좌 변경", "일일 보험"]

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Tab Navigation */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8">
            {tabs.map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`py-4 px-2 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === tab
                    ? "border-blue-500 text-blue-600"
                    : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                }`}
              >
                {tab}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {activeTab === "내 보험" && (
          <div className="space-y-6">
            {/* 자동차종합보험 */}
            <Card className="bg-white shadow-sm">
              <CardContent className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                    <div>
                      <h3 className="text-lg font-bold text-gray-900">자동차종합보험</h3>
                      <p className="text-sm text-gray-600">삼성화재 • 종합보험</p>
                    </div>
                  </div>
                  <Badge variant="secondary" className="bg-gray-900 text-white">
                    가입중
                  </Badge>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-2">보험료 정보</h4>
                    <div className="text-2xl font-bold text-blue-600 mb-1">125,000원</div>
                    <div className="text-sm text-gray-600 mb-1">매월 10일</div>
                    <div className="text-sm text-gray-600">납부계좌: 하나은행 123-456789-01</div>
                  </div>

                  <div>
                    <h4 className="font-semibold text-gray-900 mb-3">보장내용</h4>
                    <div className="flex flex-wrap gap-2">
                      <Badge variant="outline" className="text-xs">
                        대인배상
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        대물배상
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        자기신체사고
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        자기차량손해
                      </Badge>
                    </div>
                  </div>
                </div>

                <div className="flex space-x-3 mt-6">
                  <Button variant="outline" size="sm" className="flex items-center space-x-1 bg-transparent">
                    <ExternalLink className="w-4 h-4" />
                    <span>보험사 바로가기</span>
                  </Button>
                  <Button variant="outline" size="sm">
                    보험금 청구
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* 운전자보험 */}
            <Card className="bg-white shadow-sm">
              <CardContent className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                    <div>
                      <h3 className="text-lg font-bold text-gray-900">운전자보험</h3>
                      <p className="text-sm text-gray-600">KB손해보험 • 운전자보험</p>
                    </div>
                  </div>
                  <Badge variant="secondary" className="bg-gray-900 text-white">
                    가입중
                  </Badge>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-2">보험료 정보</h4>
                    <div className="text-2xl font-bold text-blue-600 mb-1">45,000원</div>
                    <div className="text-sm text-gray-600 mb-1">매월 15일</div>
                    <div className="text-sm text-gray-600">납부계좌: 하나은행 123-456789-01</div>
                  </div>

                  <div>
                    <h4 className="font-semibold text-gray-900 mb-3">보장내용</h4>
                    <div className="flex flex-wrap gap-2">
                      <Badge variant="outline" className="text-xs">
                        벌금
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        변호사비용
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        교통사고처리지원금
                      </Badge>
                    </div>
                  </div>
                </div>

                <div className="flex space-x-3 mt-6">
                  <Button variant="outline" size="sm" className="flex items-center space-x-1 bg-transparent">
                    <ExternalLink className="w-4 h-4" />
                    <span>보험사 바로가기</span>
                  </Button>
                  <Button variant="outline" size="sm">
                    보험금 청구
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {activeTab === "계좌 변경" && (
          <div className="text-center py-12">
            <p className="text-gray-500">계좌 변경 기능을 준비 중입니다.</p>
          </div>
        )}

        {activeTab === "일일 보험" && (
          <div className="text-center py-12">
            <p className="text-gray-500">일일 보험 기능을 준비 중입니다.</p>
          </div>
        )}
      </main>
    </div>
  )
}
