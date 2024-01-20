function openBoardForm() {
    document.getElementById("boardForm").style.display = "block";
}

function closeBoardForm() {
    document.getElementById("boardForm").style.display = "none";
}

function openUserForm() {
    document.getElementById("userForm").style.display = "block";
}

function closeUserForm() {
    document.getElementById("userForm").style.display = "none";
}

function submitBoardForm() {
    const boardName = document.getElementById("boardName").value;
    const boardColor = document.getElementById("boardColor").value;
    const boardDescription = document.getElementById("boardDescription").value;
    const participantsInput = document.getElementById("participants");
    let participants = [];

    if (participantsInput != null && participantsInput.value.indexOf(",") !== -1) {
        participants = participantsInput.value.split(",").map(participant => participant.trim());
    }

    const data = {
        boardName: boardName,
        boardColor: boardColor,
        boardDescription: boardDescription,
        participants: participants
    };

    fetch('/api/users/boards', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('보드 생성에 실패했습니다.');
            }
        })
        .then(data => {
            console.log('Success:', data);
            closeBoardForm();
        })
        .catch((error) => {
            console.error('Error:', error.message);
            document.getElementById("boardFormErrorMessage").textContent = "보드 생성에 실패했습니다.";
        });
}

function submitUserForm() {
    const name = document.getElementById("name").value;
    const phone = document.getElementById("phone").value;
    const userId = document.querySelector('[data-userId]').getAttribute('data-userId');

    const data = {
        name: name,
        phone: phone
    };

    fetch(`/api/users/${userId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            closeUserForm();
        })
        .catch(error => {
            console.error("Error updating board:", error);
            document.getElementById("editUserFormErrorMessage").textContent = "사용자 정보 수정에 실패했습니다.";
        });
}

function deleteUser() {
    const userId = document.querySelector('[data-userId]').getAttribute('data-userId');

    fetch(`/api/users/${userId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(response => {
            if (response.ok) {
                console.log('User deleted successfully');
                alert('회원탈퇴가 완료되었습니다.');
                window.location.href = '/api/user/login-page';
            } else {
                console.error('Failed to delete user');
                document.getElementById("deleteUserErrorMessage").textContent = "사용자를 삭제할 수 없습니다.";
            }
        })
        .catch(error => {
            console.error('Error deleting user:', error);
        });
}

function deleteBoard() {
    const boardId = document.querySelector('[data-boardId]').getAttribute('data-boardId');

    fetch(`/api/users/boards/${boardId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(response => {
            if (response.ok) {
                console.log('User deleted successfully');
                alert('보드 삭제가 완료되었습니다.');
                window.location.href = '/';
            } else {
                console.error('Failed to delete user');
                document.getElementById("deleteUserErrorMessage").textContent = "보드를 삭제할 수 없습니다.";
            }
        })
        .catch(error => {
            console.error('Error deleting user:', error);
        });
}