$(document).ready(function () {
    Cookies.remove('Authorization', {path: '/'});
});

const host = 'http://' + window.location.host;

const href = location.href;
const queryString = href.substring(href.indexOf("?")+1)
if (queryString === 'error') {
    const errorDiv = document.getElementById('login-failed');
    errorDiv.style.display = 'block';
}

document.addEventListener("DOMContentLoaded", function() {
    const signUpBtn = document.getElementById("signUp");
    const signIn = document.getElementById("signIn");
    const signInBtn = document.getElementById("signInBtn");
    const container = document.querySelector(".container");

    signUpBtn.addEventListener("click", function() {
        container.classList.add("right-panel-active");
    });

    signIn.addEventListener("click", function() {
        container.classList.remove("right-panel-active");
    });

    signInBtn.addEventListener("click", function() {
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
                showLoginFailedMessage();
            });
    });

    $('#signUpBtn').click(function () {
        const name = $('#name').val();
        const nickname = $('#nickname').val();
        const email = $('#signupEmail').val();
        const phone = $('#phone').val();
        const password = $('#signupPassword').val();

        if (!name || !email || !phone || !password) {
            showSignupWarningMessage();
            return;
        }

        $.ajax({
            type: "POST",
            url: '/api/join',
            contentType: "application/json",
            data: JSON.stringify({
                name: name,
                nickname: nickname,
                email: email,
                phone: phone,
                password: password,
                auth: 'USER'
            }),
        })
            .done(function (res, status, xhr) {
                showSignupSuccessMessage();
                console.log('Signup successful');
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.log('Signup failed');
            });
    });
});

function showSignupWarningMessage() {
    const signupWarningMessage = document.getElementById('signupWarningMessage');
    signupWarningMessage.style.display = 'block';
}

function showSignupSuccessMessage() {
    const signupSuccessMessage = document.getElementById('signupSuccessMessage');
    signupSuccessMessage.style.display = 'block';
}

function showLoginFailedMessage() {
    const loginFailedMessage = document.getElementById('loginFailedMessage');
    loginFailedMessage.style.display = 'block';
}