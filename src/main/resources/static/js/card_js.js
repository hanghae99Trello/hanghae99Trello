// 댓글 추가
function openCommentAddForm() {
    document.getElementById("CommentAddForm").style.display = "block";
}

function closeCommentAddForm() {
    document.getElementById("CommentAddForm").style.display = "none";
}

function submitCommentForm() {
    const commentMessage = document.getElementById("commentMessage").value;
    const cardContainer = document.querySelector('.card_container');
    const boardId = cardContainer.getAttribute('data-board-id');
    const columnId = cardContainer.getAttribute('data-column-id');
    const cardId = cardContainer.getAttribute('data-card-id');

    const data = {
        commentMessage: commentMessage
    };

    fetch(`/api/users/boards/${boardId}/columns/${columnId}/cards/${cardId}/comments`, {
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
            console.log('Success creating comment:', data);
            closeCommentAddForm();
        })
        .catch((error) => {
            console.error('Error creating comment:', error.message);
            document.getElementById("CommentFormErrorMessage").textContent = "댓글 생성에 실패했습니다.";
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
            console.log('Success updating user:', data);
            closeUserEditForm();
        })
        .catch(error => {
            console.error("Error updating user:", error);
            document.getElementById("editUserFormErrorMessage").textContent = "사용자 정보 수정에 실패했습니다.";
        });
}


