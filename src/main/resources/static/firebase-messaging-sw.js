//<script type="module">
  // Import the functions you need from the SDKs you need
  import { initializeApp } from "https://www.gstatic.com/firebasejs/9.6.7/firebase-app.js";
  import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.6.7/firebase-analytics.js";
  // TODO: Add SDKs for Firebase products that you want to use
  // https://firebase.google.com/docs/web/setup#available-libraries

  // Your web app's Firebase configuration
  // For Firebase JS SDK v7.20.0 and later, measurementId is optional
  const firebaseConfig = {
    apiKey: "AIzaSyDYMihnzhL7d91pBplGzo4jyojXu27mOnw",
    authDomain: "gomingout.firebaseapp.com",
    projectId: "gomingout",
    storageBucket: "gomingout.appspot.com",
    messagingSenderId: "69630148561",
    appId: "1:69630148561:web:168a153001af96ac71eaf0",
    measurementId: "G-FVLX3CVC8M"
  };

  // Initialize Firebase
  const app = initializeApp(firebaseConfig);
  const analytics = getAnalytics(app);
//</script>