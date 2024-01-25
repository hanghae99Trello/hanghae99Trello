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
            location.reload();
        })
        .catch((error) => {
            console.error('Error creating comment:', error.message);
            document.getElementById("CommentFormErrorMessage").textContent = "댓글 생성에 실패했습니다.";
        });
}


// 카드 정보 수정
function openCardEditForm() {
    const cardContainer = document.querySelector('.card_container');
    const cardId = cardContainer.getAttribute('data-card-id');
    document.getElementById("CardEditForm-" + cardId).style.display = "block";
}

function closeCardEditForm() {
    const cardContainer = document.querySelector('.card_container');
    const cardId = cardContainer.getAttribute('data-card-id');
    document.getElementById("CardEditForm-" + cardId).style.display = "none";
}

function submitCardEditForm() {
    const cardContainer = document.querySelector('.card_container');
    const boardId = cardContainer.getAttribute('data-board-id');
    const columnId = cardContainer.getAttribute('data-column-id');
    const cardId = cardContainer.getAttribute('data-card-id');
    const cardName = document.getElementById("cardName-" + cardId).value;
    const cardDescription = document.getElementById("cardDescription-" + cardId).value;
    const color = document.getElementById("color-" + cardId).value;
    const dueDate = document.getElementById("dueDate-" + cardId).value;
    const operatorInput = document.getElementById("operatorNicknames-" + cardId);
    let operatorNicknames = [];

    if (operatorInput != null && operatorInput.value != null) {
        const inputValues = operatorInput.value.split(",");

        if (inputValues.length === 1) {
            operatorNicknames.push(inputValues[0].trim());
        }

        operatorNicknames = inputValues.map(operatorNickname => operatorNickname.trim());
    }

    const data = {
        cardName: cardName,
        cardDescription: cardDescription,
        color: color,
        dueDate: dueDate,
        operatorNames: operatorNicknames
    };

    fetch(`/api/users/boards/${boardId}/columns/${columnId}/cards/${cardId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success updating card:', data);
            closeCardEditForm();
            location.reload();
        })
        .catch(error => {
            console.error("Error updating card:", error);
            document.getElementById("editCardFormErrorMessage").textContent = "카드 정보 수정에 실패했습니다.";
        });
}


