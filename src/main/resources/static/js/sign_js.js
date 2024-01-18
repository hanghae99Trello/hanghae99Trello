document.addEventListener("DOMContentLoaded", function() {
    const signUpBtn = document.getElementById("signUp");
    const signInBtn = document.getElementById("signInBtn"); // 'signIn'이 아닌 'signInBtn'로 수정
    const container = document.querySelector(".container");

    signUpBtn.addEventListener("click", function() {
        container.classList.add("right-panel-active");
    });

    signInBtn.addEventListener("click", function() {
        container.classList.remove("right-panel-active");

        // 변경된 부분: 'onLogin()' 함수를 호출하지 않고, 로그인 요청 로직을 직접 처리
        let email = $('#email').val();
        let password = $('#password').val();

        $.ajax({
            type: "POST",
            url: `/api/user/login`,
            contentType: "application/json",
            data: JSON.stringify({username: email, password: password}),
        })
            .done(function (res, status, xhr) {
                window.location.href = host;
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.log('statusCode: ' + xhr.status);
                window.location.href = host + '/api/user/login-page?error'
            });
    });
});

$(document).ready(function () {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
});

const host = 'http://' + window.location.host;

const href = location.href;
const queryString = href.substring(href.indexOf("?")+1)
if (queryString === 'error') {
    const errorDiv = document.getElementById('login-failed');
    errorDiv.style.display = 'block';
}

function onLogin() {
    let email = $('#email').val();
    let password = $('#password').val();

    $.ajax({
        type: "POST",
        url: `/api/user/login`,
        contentType: "application/json",
        data: JSON.stringify({username: email, password: password}),
    })
        .done(function (res, status, xhr) {
            window.location.href = host;
        })
        .fail(function (xhr, textStatus, errorThrown) {
            console.log('statusCode: ' + xhr.status);
            window.location.href = host + '/api/user/login-page?error'
        });
}