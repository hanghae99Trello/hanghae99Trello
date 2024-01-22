// 보드 생성
function openBoardAddForm() {
    document.getElementById("BoardAddForm").style.display = "block";
}

function closeBoardAddForm() {
    document.getElementById("BoardAddForm").style.display = "none";
}

function submitBoardForm() {
    const boardName = document.getElementById("boardName").value;
    const boardColor = document.getElementById("boardColor").value;
    const boardDescription = document.getElementById("boardDescription").value;
    const participantsInput = document.getElementById("participants");
    let participants = [];

    if (participantsInput != null && participantsInput.value != null) {
        const inputValues = participantsInput.value.split(",");

        if (inputValues.length === 1) {
            participants.push(inputValues[0].trim());
        }

        participants = inputValues.map(participant => participant.trim());
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
            }
        })
        .then(data => {
            console.log('Success:', data);
            closeBoardAddForm();
        })
        .catch((error) => {
            console.error('Error:', error.message);
            document.getElementById("boardFormErrorMessage").textContent = "보드 생성에 실패했습니다.";
        });
}

function deleteBoard(button) {
    const boardId = button.getAttribute('data-board-id');

    fetch(`/api/users/boards/${boardId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(response => {
            if (response.ok) {
                console.log(`Board ${boardId} deleted successfully`);
                alert('보드 삭제가 완료되었습니다.');
                // Optionally, you can remove the deleted board from the DOM
                button.parentNode.remove();
            } else {
                console.error('Failed to delete board');
                document.getElementById("deleteUserErrorMessage").textContent = "보드를 삭제할 수 없습니다.";
            }
        })
        .catch(error => {
            console.error('Error deleting board:', error);
        });
}


// 사용자 정보 수정
function openUserEditForm() {
    document.getElementById("UserEditForm").style.display = "block";
}

function closeUserEditForm() {
    document.getElementById("UserEditForm").style.display = "none";
}

function submitUserEditForm() {
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
            closeUserEditForm();
        })
        .catch(error => {
            console.error("Error updating board:", error);
            document.getElementById("editUserFormErrorMessage").textContent = "사용자 정보 수정에 실패했습니다.";
        });
}


// 사용자 삭제
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
