<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
</head>
<script>
var isIdAvailable = false; // 아이디 중복 확인 여부

//아이디 중복체크
function checkId() {
 $.ajax({
     url: "/checkID",
     type: "post",
     cache: false,
     dataType: "html",
     data: {"id": $("#id").val()},
     success: function (data) {
         if (data == 0) {                
             alert("가입할 수 있는 ID입니다.");
             isIdAvailable = true; // 아이디 사용 가능
             $("#submitBtn").attr("disabled", false);
         } else {
             alert("이미 가입되어 있는 ID입니다.");
             isIdAvailable = false; // 아이디 사용 불가능
             $("#submitBtn").attr("disabled", true);
         }
     },
     error: function (request, status, error) {
         alert("문제가 발생하였습니다." + error);
     }
 });
}

//회원가입
function submitok() {
 // 아이디 중복 확인 여부 확인
 if (!isIdAvailable) {
     alert("아이디 중복 확인을 해주세요.");
     return;
 }

 // 회원가입
 $.ajax({
     type: "POST",
     url: "/signgo",
     contentType: "application/json; charset=utf-8",
     data: JSON.stringify({
         id: $("#id").val(),
         password: $("#password").val(),
         email: $("#email").val(),
         name: $("#text").val(),
         role: $("#role").val()
     }),
     success: function (data) {
         // 성공했을 때 수행할 동작
         alert("회원가입이 완료되었습니다.");
         location.href = "/index";
     },
     error: function (error) {
         // 실패했을 때 수행할 동작
         alert("회원가입이 실패하였습니다.");
         console.error(error);
     }
 });
}



</script>

<body>
    <div class="flex flex-col h-screen">
        <div class="mt-10 flex-grow mb-10">
            <div class="rounded-lg border bg-card text-card-foreground shadow-sm max-w-2xl mx-auto" data-v0-t="card">
                <div class="flex flex-col space-y-1.5 p-6">
                    <h3 class="text-2xl font-semibold leading-none tracking-tight">회원 가입</h3>
                    <p class="text-sm text-muted-foreground">사용자 정보를 입력하세요.</p>
                </div>
                <div class="p-6">
                    <!-- 사용자 등록 양식 -->
                    <div class="space-y-4">
                        <div class="flex items-center space-x-2">
                            <div class="space-y-2 flex-grow">
                                <label
                                    class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                    for="id"
                                >
                                    아이디
                                </label>
                                <input
                                    class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                    id="id"
                                    placeholder="아이디 입력해주세요"
                                    required=""
                                    type="text"
                                />
                            </div>
                            <button
                                class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded h-full focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
                                onclick="checkId()"
                                style="margin-top: 30px;"
                            >
                                중복 확인
                            </button>
                        </div>
                        <div class="space-y-2">
                            <label
                                class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                for="password"
                            >
                                비밀번호
                            </label>
                            <input
                                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                id="password"
                                required=""
                                type="password"
                            />
                        </div>
                        <div class="space-y-2">
                            <label
                                class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                for="email"
                            >
                                이메일
                            </label>
                            <input
                                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                id="email"
                                placeholder="johndoe@example.com"
                                required=""
                                type="email"
                            />
                        </div>
                        <div class="space-y-2">
                            <label
                                class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                for="text"
                            >
                                이름
                            </label>
                            <input
                                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                id="text"
                                placeholder="홍길동"
                                required=""
                                type="text"
                            />
                        </div>
                        <div class="space-y-2">
                            <label
                                class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                for="role"
                            >
                                역할
                            </label>
                            <div class="relative inline-block w-full text-gray-700">
                                <select
                                    id="role"
                                    name="role"
                                    class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    <option value="user">사용자</option>
                                    <option value="admin">관리자</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="flex items-center justify-center p-6">
                        <button
                            class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
                            type="submit"
                            onclick="submitok()"
                            id="submitBtn"
	                        >
                            사용자 생성
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
