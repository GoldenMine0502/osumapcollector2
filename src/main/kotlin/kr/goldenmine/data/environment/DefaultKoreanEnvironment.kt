package kr.goldenmine.data.environment

import javax.inject.Inject

class DefaultKoreanEnvironment @Inject constructor(): DefaultEnvironment() {
    override val textName = "사용자 이름"
    override val textPassword = "비밀번호"
    override val textLogin = "로그인"
    override val textUnavilable = "여기 계시면 안됩니다."
}