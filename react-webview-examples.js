// React에서 안드로이드 WebView 기능 사용 예제

// 1. 멀티파일 선택 + 카메라 (웹 표준 API - 예외처리 불필요)
function FileUpload() {
  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    console.log('선택된 파일들:', files);
  };

  return (
    <div>
      {/* 멀티파일 + 카메라 */}
      <input 
        type="file" 
        multiple 
        accept="image/*,video/*" 
        capture="environment" // 후면 카메라
        onChange={handleFileSelect}
      />
      
      {/* 이미지만 + 카메라 */}
      <input 
        type="file" 
        accept="image/*" 
        capture="camera" 
        onChange={handleFileSelect}
      />
      
      {/* 비디오만 + 카메라 */}
      <input 
        type="file" 
        accept="video/*" 
        capture="camcorder" 
        onChange={handleFileSelect}
      />
    </div>
  );
}

// 2. 다운로드 기능 (웹 표준 API - 예외처리 불필요)
function DownloadButton() {
  const handleDownload = () => {
    // 방법 1: 직접 링크
    const link = document.createElement('a');
    link.href = 'https://example.com/file.pdf';
    link.download = 'my-file.pdf';
    link.click();
    
    // 방법 2: Blob 다운로드
    const blob = new Blob(['Hello World'], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link2 = document.createElement('a');
    link2.href = url;
    link2.download = 'hello.txt';
    link2.click();
    URL.revokeObjectURL(url);
  };

  return <button onClick={handleDownload}>파일 다운로드</button>;
}

// 3. 웹페이지 캡처 기능 (안드로이드 전용 - 예외처리 필요)
function WebCapture() {
  const captureWebPage = async () => {
    try {
      // 방법 1: 커스텀 API 사용 (안드로이드 WebView 전용)
      if (window.saveWebPageAsImage) {
        await window.saveWebPageAsImage();
        return;
      }
      
      // 방법 2: 직접 호출 (안드로이드 WebView 전용)
      if (window.Android && window.Android.captureWebPage) {
        window.Android.captureWebPage();
        return;
      }
      
      // 방법 3: 일반 브라우저에서는 html2canvas 등 사용
      if (typeof html2canvas !== 'undefined') {
        const canvas = await html2canvas(document.body);
        canvas.toBlob((blob) => {
          const link = document.createElement('a');
          link.href = URL.createObjectURL(blob);
          link.download = 'webpage-capture.png';
          link.click();
        });
      } else {
        alert('웹페이지 캡처 기능은 앱에서만 지원됩니다.');
      }
    } catch (error) {
      console.error('캡처 실패:', error);
      alert('웹페이지 캡처에 실패했습니다.');
    }
  };

  return <button onClick={captureWebPage}>웹페이지 캡처</button>;
}

// 4. 안드로이드 WebView 감지 유틸리티
const isAndroidWebView = () => {
  return !!(window.Android && window.Android.captureWebPage);
};

// 5. 플랫폼별 분기 처리 예제
function PlatformSpecificFeature() {
  const handleAction = () => {
    if (isAndroidWebView()) {
      // 안드로이드 WebView에서만 가능한 기능
      window.Android.captureWebPage();
    } else {
      // 일반 브라우저에서는 다른 방법 사용
      alert('이 기능은 앱에서만 사용 가능합니다.');
    }
  };

  return (
    <button onClick={handleAction}>
      {isAndroidWebView() ? '웹페이지 캡처' : '앱에서 사용 가능'}
    </button>
  );
}

export { FileUpload, DownloadButton, WebCapture, PlatformSpecificFeature };
