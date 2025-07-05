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

// 6. Pull to Refresh 기능 (안드로이드 네이티브에서 처리)
function PullToRefreshInfo() {
  return (
    <div style={{padding: '20px', backgroundColor: '#f5f5f5', margin: '10px 0'}}>
      <h3>📱 Pull to Refresh 기능</h3>
      <p>안드로이드 앱에서는 페이지를 아래로 당기면 새로고침됩니다.</p>
      <p>브라우저에서는 기본적으로 지원되지 않으므로, 일반적인 새로고침 버튼을 사용하세요.</p>
      
      <div style={{marginTop: '15px', padding: '15px', backgroundColor: '#e8f4f8', borderRadius: '8px'}}>
        <h4>📖 작동 방식</h4>
        <ul style={{textAlign: 'left', marginTop: '10px'}}>
          <li><strong>페이지 최상단에서만 활성화:</strong> 스크롤이 맨 위에 있을 때만 Pull to Refresh 가능</li>
          <li><strong>자동 감지:</strong> JavaScript로 스크롤 위치를 실시간 감지</li>
          <li><strong>충돌 방지:</strong> WebView 스크롤과 Pull to Refresh 제스처 분리</li>
        </ul>
      </div>
      
      <button onClick={() => window.location.reload()}>
        🔄 페이지 새로고침 (테스트용)
      </button>
    </div>
  );
}

// 7. 이미지 뷰어 기능 (안드로이드 네이티브에서 처리)
function ImageViewerDemo() {
  const sampleImages = [
    'https://picsum.photos/800/600?random=1',
    'https://picsum.photos/800/600?random=2',
    'https://picsum.photos/800/600?random=3'
  ];

  const handleImageClick = (imageUrl) => {
    // 안드로이드 앱에서는 네이티브 이미지 뷰어 호출
    if (window.Android && window.Android.showImageViewer) {
      // 네이티브 이미지 뷰어 직접 호출
      window.Android.showImageViewer(imageUrl);
    } else {
      // 브라우저에서는 새 창으로 열기
      window.open(imageUrl, '_blank');
    }
  };

  return (
    <div style={{padding: '20px'}}>
      <h3>🖼️ 이미지 뷰어 기능</h3>
      <p>이미지를 클릭하면 전체화면으로 볼 수 있습니다.</p>
      <p>안드로이드 앱에서는 확대/축소, 더블탭 줌 기능이 지원됩니다.</p>
      
      <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '10px'}}>
        {sampleImages.map((imageUrl, index) => (
          <div key={index} style={{textAlign: 'center'}}>
            <img 
              src={imageUrl} 
              alt={`Sample ${index + 1}`}
              style={{
                width: '100%',
                height: '150px',
                objectFit: 'cover',
                cursor: 'pointer',
                border: '2px solid #ddd',
                borderRadius: '8px'
              }}
              onClick={() => handleImageClick(imageUrl)}
            />
            <p style={{marginTop: '8px', fontSize: '14px'}}>
              이미지 {index + 1} - 클릭하여 확대
            </p>
          </div>
        ))}
      </div>
      
      <div style={{marginTop: '20px', padding: '15px', backgroundColor: '#e8f4f8', borderRadius: '8px'}}>
        <h4>📖 사용법</h4>
        <ul style={{textAlign: 'left', marginTop: '10px'}}>
          <li><strong>안드로이드 앱:</strong> 이미지 클릭 → 네이티브 이미지 뷰어 호출</li>
          <li><strong>더블탭:</strong> 2배 확대/축소</li>
          <li><strong>핀치:</strong> 자유로운 확대/축소 (0.1x ~ 10x)</li>
          <li><strong>탭:</strong> 뷰어 닫기</li>
          <li><strong>뒤로가기:</strong> 뷰어 닫기</li>
        </ul>
        
        <div style={{marginTop: '15px', padding: '10px', backgroundColor: '#fff3cd', borderRadius: '4px'}}>
          <p style={{fontSize: '14px', margin: 0}}>
            <strong>⚠️ 주의:</strong> 안드로이드 앱에서는 JavaScript에서 
            <code>window.Android.showImageViewer()</code>를 호출하여 네이티브 이미지 뷰어를 실행합니다.
          </p>
        </div>
      </div>
    </div>
  );
}

// 8. 브라우저 vs 앱 기능 비교
function FeatureComparison() {
  const isAndroidApp = !!(window.Android);
  
  return (
    <div style={{padding: '20px', backgroundColor: '#f9f9f9', margin: '10px 0'}}>
      <h3>📊 브라우저 vs 앱 기능 비교</h3>
      <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '15px'}}>
        
        <div style={{padding: '15px', backgroundColor: '#fff', borderRadius: '8px', border: '1px solid #ddd'}}>
          <h4>🌐 브라우저</h4>
          <ul style={{textAlign: 'left'}}>
            <li>✅ 파일 업로드 (멀티)</li>
            <li>✅ 카메라 캡처</li>
            <li>✅ 파일 다운로드</li>
            <li>✅ 이미지 새창 열기</li>
            <li>❌ Pull to Refresh</li>
            <li>❌ 웹페이지 캡처</li>
            <li>❌ 이미지 뷰어 (제스처)</li>
          </ul>
        </div>
        
        <div style={{padding: '15px', backgroundColor: '#fff', borderRadius: '8px', border: '1px solid #ddd'}}>
          <h4>📱 안드로이드 앱</h4>
          <ul style={{textAlign: 'left'}}>
            <li>✅ 파일 업로드 (멀티)</li>
            <li>✅ 카메라 캡처</li>
            <li>✅ 파일 다운로드</li>
            <li>✅ Pull to Refresh</li>
            <li>✅ 웹페이지 캡처</li>
            <li>✅ 이미지 뷰어 (제스처)</li>
            <li>✅ 확대/축소 지원</li>
          </ul>
        </div>
      </div>
      
      <div style={{marginTop: '15px', padding: '15px', backgroundColor: isAndroidApp ? '#e8f5e8' : '#fff3cd', borderRadius: '8px'}}>
        <p style={{fontWeight: 'bold', color: isAndroidApp ? '#155724' : '#856404'}}>
          현재 환경: {isAndroidApp ? '📱 안드로이드 앱' : '🌐 브라우저'}
        </p>
      </div>
    </div>
  );
}

export { 
  FileUpload, 
  DownloadButton, 
  WebCapture, 
  PlatformSpecificFeature, 
  PullToRefreshInfo, 
  ImageViewerDemo, 
  FeatureComparison 
};
