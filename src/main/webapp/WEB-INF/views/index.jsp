<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
  <script src="https://cdn.tailwindcss.com?plugins=forms,typography"></script>
</head>
<script>
function logout() {
    // You can add any additional logic here before submitting the form if needed
    document.getElementById("logoutForm").submit();
}

function redirectToLogin() {
    window.location.href = '/login';
}
</script>


<body>

<button class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
onclick ="location.href='signup'" >회원가입</button><br>

<button>게시판</button><br>
<!-- 로그아웃 폼 -->
<form id="logoutForm" action="/logout" method="post">
    <button type="submit" onclick="logout()">로그아웃</button>
</form>




</body>

</html>