<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
    /* 살짝 띄워진 스타일 추가 */
    button {
        margin-right: 10px;
    }
</style>
<script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>  
</head>
<script>
function logout() {
    document.getElementById("logoutForm").submit();
}

function openBoard() {
    window.open('http://localhost:9090/board', '_blank');
}
</script>

<body>

<button class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
    onclick="location.href='signup'">회원가입</button>
    <br>

<button class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
    onclick="openBoard()">게시판</button>

<!-- 로그아웃 폼 -->
<form id="logoutForm" action="/logout" method="post">
    <button type="button" onclick="logout()" class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2">로그아웃</button>
</form>

</body>
</html>
