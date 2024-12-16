document.addEventListener('DOMContentLoaded', () => {
    let page = 0; // Initial page number
    const size = 10; // Number of posts per request
    let loading = false;
    let notificationPage = 0;
    let notificationLoading = false;
    loadMorePosts();

    // scroll đến cuối trang thì load thêm post
    const mainFrame = document.getElementById("main");
    mainFrame.addEventListener('scroll', handleScroll);
    const notificationFrame = document.querySelector('.notification-list');
    notificationFrame.addEventListener('scroll', notificationHandleScroll);
    const postButton = document.getElementById("postButton");
    const eventSource = new EventSource('/stream');
    // const commentEventSource = new EventSource('/comment/stream');
    const pageUserElement = document.getElementById("page_user");
    const currentUserId = pageUserElement.dataset.userId;
    const recipientEventSource = new EventSource(`/stream/${currentUserId}`);
    const notificationButton = document.getElementById("page_notification");
    notificationButton.addEventListener('click', toggleNotificationPopup);
    const notificationPopup = document.querySelector(".notification-popup");
    // loadMoreNotification();
    function toggleNotificationPopup() {
        if (notificationPopup.style.display === "none" || notificationPopup.style.display === "") {
            const notificationList = notificationPopup.querySelector('.notification-list');
            if (notificationList) {
                notificationList.innerHTML = ""; // Chỉ xóa nội dung của notification-list
            }
            notificationPopup.style.display = "block";
            notificationPage=0;
            loadMoreNotification();
            resetNotificationCount(currentUserId);
        } else {
            notificationPopup.style.display = "none";
        }
    }
    document.addEventListener("click", function (event) {
        const isClickInside = notificationButton.contains(event.target) || notificationPopup.contains(event.target);

        if (!isClickInside) {
            notificationPopup.style.display = "none";
        }
    });
    //delegation notification
    document.querySelector('.notification-popup').addEventListener('click', (event) => {
        const target = event.target;
        if (target.closest('.notification-item') && !target.closest('.delete-btn')) {
            const notificationItem = target.closest('.notification-item');
            const postId = notificationItem.dataset.postId;
            const notificationId = notificationItem.dataset.notificationId;
            mainPopup();
            readNotification(notificationId);
            renderPostsToMainPopup(postId);
        }
        if (target.closest('.delete-btn')) {
            const notificationItem = target.closest('.notification-item');
            const notificationId = notificationItem.dataset.notificationId;
            fetch(`/notification/delete/${notificationId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (response.ok) {
                        // Xóa phần tử notification khỏi DOM nếu thành công
                        notificationItem.remove();
                    } else {
                        console.error('Error deleting notification');
                    }
                })
                .catch(error => {
                    console.error('Network error:', error);
                });

        }
    })

    function mainPopup() {
        const mainPopupElement = document.querySelector('.main.popup');
        mainPopupElement.classList.add('show');
    }
    // delegation post
    document.querySelector('.main').addEventListener('click', (event) => {
        const target = event.target;

        // Like button
        if (target.closest('.control.like')) {
            const postId = target.closest('.post').getAttribute('data-id');
            likePost(postId);
        }

        // Comment button
        if (target.closest('.control.comment')) {
            const post = target.closest('.post');
            const commentArea = post.querySelector('.post_comment_area');
            if (commentArea) {
                commentArea.classList.toggle('show'); // Toggle comment area visibility
            }
        }
        //show edit popup
        if (target.closest('.control.edit')) {
            const post = target.closest('.post');
            const editPopup = post.querySelector('.popup_edit');
            editPopup.classList.toggle("popup_edit_show");
        }
        // delete post button
        if (target.closest('.control.delete')) {
            const postId = target.closest('.post').getAttribute('data-id');
            deletePost(postId);
        }

        // Send comment button
        if (target.id === 'comment-send') {
            const postElement = target.closest('.post');
            const postId = postElement.getAttribute('data-id');
            const commentContentElement = postElement.querySelector('#comment_content');
            const commentText = commentContentElement.value.trim();
            if (commentText) {
                createComment(postId, commentText); // Call a function to create a new comment
                commentContentElement.value = ''; // Clear the text area
            } else {
                alert("Please enter a comment.");
            }
        }
        //đóng mainpopup
        if (target.id === 'close_button') {
            const mainPopupElement = document.querySelector('.main.popup');
            mainPopupElement.classList.remove('show');
        }
    });
    async function createPostHTML(postId, currentUserId) {
        try {
            const response = await fetch(`/render/post?postId=${postId}&currentUserId=${currentUserId}`);
            if (!response.ok) throw new Error("Failed to load post fragment");

            // Get the HTML content of the post fragment as a string
            const postHTML = await response.text();
            return postHTML;
        } catch (error) {
            console.error("Error fetching post HTML fragment:", error);
            return ""; // Return empty string if there's an error
        }
    }
    // chỗ này bắt đầu phân trang

    async function loadMorePosts() {
        if (loading) return; // Prevent multiple requests while loading
        loading = true;
        // document.getElementById("loadingIndicator").style.display = "block";
        console.log("loading = true")

        try {
            const response = await fetch(`/post/posts?page=${page}&size=${size}`);
            const newPosts = await response.json();
            console.log("vừa mới fetch")

            if (newPosts.length > 0) {
                page++; // Increment page number for the next request
                console.log("mới tăng page lên 1 rồi gọi đến hàm renderpost")
                await renderPostsToMain(newPosts); // Render the newly fetched posts on the page
                await new Promise(resolve => setTimeout(resolve, 700 + Math.random() * 1000));
            } else {
                mainFrame.removeEventListener('scroll', handleScroll); // No more posts to load
            }
        } catch (error) {
            console.error("Error fetching posts:", error);
        }
        // document.getElementById("loadingIndicator").style.display = "none";
        loading = false;
    }

    function handleScroll() {
        if (mainFrame.scrollTop + mainFrame.clientHeight >= mainFrame.scrollHeight - 100) {
            console.log("Kéo đến cuối danh sách bài viết, gọi loadMorePosts");
            loadMorePosts();
        }
    }
    function notificationHandleScroll() {
        if (notificationFrame.scrollTop + notificationFrame.clientHeight >= notificationFrame.scrollHeight - 100) {
            loadMoreNotification();
        }
    }



    // Function to render posts dynamically
    async function renderPostsToMain(posts) {
        const mainContainer = document.querySelector('.main');

        for (const post of posts) {
            // Check if the post is already rendered
            if (!document.querySelector(`.post[data-id="${post.postId}"]`)) {
                // Fetch the HTML fragment for this post
                const postHTML = await createPostHTML(post.postId, currentUserId);
                // Insert the HTML into the main container
                mainContainer.insertAdjacentHTML('beforeend', postHTML);
            }
        }
    }
    async function renderPostsToMainPopup(postId) {
        const mainPopupContainer = document.querySelector('.main.popup');
        const contentContainer = mainPopupContainer.querySelector('.contentContainer');
        try {
            contentContainer.innerHTML = '';
            const postHTML = await createPostHTML(postId, currentUserId);
            contentContainer.insertAdjacentHTML('beforeend', postHTML);
        } catch (error) {
            console.log(error);
        }

    }
    async function loadMoreNotification() {
        if (notificationLoading) return; // Prevent multiple requests while loading
        notificationLoading = true;
        // document.getElementById("loadingIndicator").style.display = "block";
        console.log("notificationLoading = true")

        try {
            const response = await fetch(`/notification/notifications?recipientId=${currentUserId}&page=${notificationPage}&size=${size}`);
            const newNotifications = await response.json();
            console.log("vừa mới fetch");

            if (newNotifications.content.length > 0) {
                notificationPage++; // Increment page number for the next request
                console.log("mới tăng page lên 1 rồi gọi đến hàm renderNotification");
                await renderNotifications(newNotifications.content); // Render the newly fetched posts on the page
                await new Promise(resolve => setTimeout(resolve, 500 + Math.random() * 1000));
            } else {
                console.log("remove notification");
                notificationFrame.removeEventListener('scroll', handleScroll); // No more Notification to load
            }
        } catch (error) {
            console.error("Error fetching posts:", error);
        }
        // document.getElementById("loadingIndicator").style.display = "none";
        notificationLoading = false;
    }
    // Function to render posts dynamically
    async function renderNotifications(notifications) {
        console.log(notifications);
        const mainContainer = document.querySelector('.notification-list');

        for (const notification of notifications) {
            // Check if the post is already rendered
            const notificationHTML = await createNotificationHTML(notification.notificationId, currentUserId);
            console.log(notificationHTML);
            mainContainer.insertAdjacentHTML('beforeend', notificationHTML);
        }
    }
    async function createNotificationHTML(notificationId, currentUserId) {
        try {
            const response = await fetch(`/render/notification?notificationId=${notificationId}&currentUserId=${currentUserId}`);
            if (!response.ok) throw new Error("Failed to load Notification fragment");

            // Get the HTML content of the post fragment as a string
            const notificationHTML = await response.text();
            return notificationHTML;
        } catch (error) {
            console.error("Error fetching post HTML fragment:", error);
            return ""; // Return empty string if there's an error
        }
    }



    //stream unreadNotification
    recipientEventSource.addEventListener('new-unreadNoti', function (event) {
        const unreadNoti = JSON.parse(event.data);
        updateUnreadNoti(unreadNoti);
    });
    // async function streamNewNotification(notification) {
    //     // Tạo phần tử HTML mới cho thông báo
    //     const notificationList = document.querySelector(".notification-list");

    //     // Tạo nội dung HTML cho thông báo mới
    //     const notificationHTML = await createNotificationHTML(notification.notificationId, currentUserId);

    //     // Thêm thông báo mới vào đầu danh sách
    //     notificationList.insertAdjacentHTML('afterbegin', notificationHTML);
    // }
    function updateUnreadNoti(unreadNoti) {
        unreadNotiElement = document.querySelector('.unreadNoti');
        if (unreadNoti > 0) {
            unreadNotiElement.textContent = '🔴' + unreadNoti;
            unreadNotiElement.style.display = 'inline'; // Hiển thị dấu chấm đỏ với số lượng thông báo
        } else {
            unreadNotiElement.style.display = 'none'; // Ẩn dấu chấm đỏ khi không có thông báo chưa đọc
        }
    }

    //stream comment
    function newCommentSSE(newComment) {
        // Tìm postElement dựa trên postId của comment mới
        const postElement = document.querySelector(`.post[data-id="${newComment.post.postId}"]`);
        if (!postElement) return; // Nếu không tìm thấy post, thoát hàm

        // Tìm khu vực hiển thị comment của post
        const commentArea = postElement.querySelector(".comment_scroll_area");

        // Tạo phần tử mới cho comment
        const newCommentDiv = document.createElement("div");
        newCommentDiv.classList.add("comment");

        // Nội dung HTML của comment mới
        newCommentDiv.innerHTML = `
            <div class="comment_header">
                <span class="post control user">
                    <img src="${newComment.user.avatar}" alt="user" class="comment_avatar">
                </span>
                <span class="comment_username">${newComment.user.name}</span>
                <span class="comment_date">${newComment.dateCreate}</span>
                <span class="comment_time">${newComment.timeCreate}</span>
            </div>
            <div class="comment_content">${newComment.commentText}</div>
        `;

        // Thêm comment mới vào đầu danh sách comment
        commentArea.insertBefore(newCommentDiv, commentArea.firstChild);

    }

    function updatePostSSE(post) {
        const thisPost = document.querySelector(`.post[data-id='${post.postId}']`);

        if (!thisPost) {
            console.log('No post exist');
            return; // Thoát nếu bài đăng không tồn tại
        }

        // Cập nhật caption
        const captionElement = thisPost.querySelector('.post.caption');
        if (captionElement) {
            captionElement.textContent = post.postCaption; // Cập nhật caption
        }

        // Cập nhật số lượng like
        const likeElements = thisPost.querySelectorAll('.control.like span');
        if (likeElements.length > 0) {
            likeElements[0].textContent = post.likeCount; // Cập nhật số lượng like
            const likeIcon = thisPost.querySelector('.control.like img');

            // Cập nhật biểu tượng like
            if (likeIcon) {
                likeIcon.src = post.likeCount > 0 ? 'images/like.png' : 'images/nolike.png';
            }
        }

        // Cập nhật số lượng comment
        const commentElements = thisPost.querySelectorAll('.control.comment span');
        if (commentElements.length > 0) {
            commentElements[0].textContent = post.commentCount; // Cập nhật số lượng comment
        }

        // Cập nhật ngày tạo nếu cần
        const dateElement = thisPost.querySelector('.post.date_create');
        if (dateElement) {
            dateElement.textContent = post.dateCreate; // Cập nhật ngày
        }

        const timeElement = thisPost.querySelector('.post.time_create');
        if (timeElement) {
            timeElement.textContent = post.timeCreate; // Cập nhật thời gian
        }
    }

    eventSource.addEventListener('post-update', function (event) {
        const post = JSON.parse(event.data);
        updatePostSSE(post);
    });

    eventSource.addEventListener('new-comment', function (event) {
        const newComment = JSON.parse(event.data);
        newCommentSSE(newComment);
        eventSource.onerror = function (error) {
            console.error("SSE connection error:", error);
        };

    })



    //chức năng nút like

    function likePost(postId) {
        console.log("Attempting to like post with ID:", postId);
        fetch(`/post/like/${postId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    console.log('Post liked successfully!');
                    newNotification(postId, "đã thích bài viết của bạn");
                } else {
                    console.error('Failed to like post');
                }
            })
            .catch(error => console.error('Error:', error));
    }

    function createComment(postId, commentText) {
        const userId = document.getElementById("page_user").dataset.userId;
        fetch("/comment/new", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, postId, commentText })
        })
            .then(response => {
                if (response.ok) {
                    console.log("Comment created");
                    newNotification(postId, "đã bình luận về bài viết");  // Gọi hàm tạo notification
                } else {
                    console.error("Failed to create comment");
                }
            })
            .catch(error => console.error("Error:", error));
    }

    const addButton = document.querySelector('.control.newpost');
    // Show the popup when the "NewPost" button is clicked
    if (addButton && popup) {
        addButton.addEventListener('click', () => {
            // popup.style.display = 'block';
            showPopup();

        });
    }

    // Hide the popup when the "Cancel" button is clicked
    if (cancelButton && popup) {
        cancelButton.addEventListener('click', () => {
            // popup.style.display = 'none';
            hidePopup();
        });
    }

    // Handle the "Done" button
    if (postButton && popup) {
        postButton.addEventListener('click', () => {
            // Perform your actions with the input values here

            // Hide the popup
            // popup.style.display = 'none';
            hidePopup();
        });
    }
    function showPopup() {
        document.querySelector('.popup-content').classList.add('popup-show');
        document.querySelector('.popup-overlay').classList.add('popup-overlay-show');
    }
    function hidePopup() {
        document.querySelector('.popup-content').classList.remove('popup-show');
        document.querySelector('.popup-overlay').classList.remove('popup-overlay-show');
    }

    //đây là phần về việc upload ảnh hoặc video hoặc text lên hệ thống:
    document.getElementById("postButton").addEventListener("click", function () {
        const postCaption = document.getElementById("popup_caption").value;
        const fileInput = document.getElementById("post_media");
        const file = fileInput.files[0];
        // Lấy phần tử có id là "page_user"
        const pageUserElement = document.getElementById("page_user");

        // Lấy giá trị của `data-user-id` từ `dataset`
        const userId = pageUserElement.dataset.userId;

        // Tạo FormData và thêm dữ liệu vào
        const formData = new FormData();
        formData.append("postCaption", postCaption);
        formData.append("userId", userId);
        formData.append("file", file);

        // Gửi dữ liệu qua AJAX
        fetch("/post/new", {
            method: "POST",
            body: formData,
            headers: {
                "Accept": "application/json"
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                throw new Error("Create Failed");
            })
            .then(data => {
                alert("Post created successfully!");
                location.reload();
            })
            .catch(error => {
                alert("Error: " + error.message);
            });
    });


    function deletePost(postId) {
        const isConfirmed = confirm("Bạn có chắc muốn xóa?")
        if (isConfirmed) {
            fetch(`/post/delete/${postId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        const element = document.querySelector(`.post[data-id="${postId}"]`);
                        if (element) {
                            element.remove();
                        }
                        return response.text();  // Trả về text từ response
                    } else {
                        throw new Error('Delete Failed');
                    }
                })
                .then(data => {
                    console.log(data);  // In ra "Delete Complete" nếu thành công
                    // Thực hiện thêm các hành động khác nếu cần, như cập nhật UI
                })
                .catch(error => {
                    console.error(error);  // In ra lỗi nếu delete thất bại
                });
        }

    }

    // chức năng xử lý khi ấn vào notification button thì dấu hiệu noti sẽ biến mất
    function resetNotificationCount(currentUserId) {
        fetch(`/notification/reset/${currentUserId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    console.log('Notification reset successfully!');
                    const unreadNotiElement = document.querySelector('.unreadNoti');
                    unreadNotiElement.style.display = 'none';
                } else {
                    console.error('Failed to reset');
                }
            })
            .catch(error => console.error('Error:', error));
    }

    async function newNotification(postId, notificationTitle) {
        const postElement = document.querySelector(`.post[data-id="${postId}"]`);
        const userElement = postElement.querySelector(".post.control.user"); // lấy user từ post
        const recipientId = userElement.dataset.userId;// lưu biến userId
        const postCaptionElement = postElement.querySelector(".post.caption");
        const notificationContent = postCaptionElement.textContent;
        const pageUserElement = document.getElementById("page_user");
        const userAvatarUrl = pageUserElement.querySelector("img").src;// lấy về ảnh của user
        const actorUsername = pageUserElement.dataset.userName;
        const notificationData = {
            notificationImage: userAvatarUrl,
            notificationTitle: actorUsername + " " + notificationTitle,
            notificationContent: notificationContent,
            postId: Number(postId),
            actorId: Number(currentUserId),
            recipientId: Number(recipientId),
            isRead: false
        };

        try {
            const response = await fetch("/notification/new", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(notificationData),
            });

            if (response.ok) {
                const result = await response.text();
                console.log("Notification created successfully:", result);
            } else {
                console.error("Failed to create notification:", response.status, response.statusText);
            }
        } catch (error) {
            console.error("Error:", error);
        }


    }

    //send Read notificaion
    function readNotification(notificationId) {
        fetch(`/notification/read/${notificationId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    console.log('đã đọc thông báo');
                    const notificationElement = notificationFrame.querySelector(`[data-notification-id="${notificationId}"]`);
                    notificationElement.classList.remove('notification-unread');
                    notificationElement.classList.add('notification-read');
                } else {
                    console.error('Failed to read notification');
                }
            })
            .catch(error => console.error('Error:', error));
    }

})