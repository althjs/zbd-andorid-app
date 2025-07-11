#!/bin/zsh

# ZBD 라이프로그 앱 아이콘 생성 스크립트

# 필요한 아이콘 크기들
typeset -A sizes
sizes[mipmap-mdpi]=48
sizes[mipmap-hdpi]=72
sizes[mipmap-xhdpi]=96
sizes[mipmap-xxhdpi]=144
sizes[mipmap-xxxhdpi]=192

# 기본 SVG 아이콘 생성
cat > /tmp/zbd_icon.svg << 'EOF'
<svg width="192" height="192" viewBox="0 0 192 192" xmlns="http://www.w3.org/2000/svg">
  <!-- 그라데이션 배경 -->
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#4A90E2;stop-opacity:1" />
      <stop offset="100%" style="stop-color:#5BA0F2;stop-opacity:1" />
    </linearGradient>
  </defs>
  
  <!-- 배경 -->
  <rect width="192" height="192" rx="24" fill="url(#bg)"/>
  
  <!-- 일기장 -->
  <rect x="36" y="36" width="120" height="120" rx="8" fill="white" stroke="#E0E0E0" stroke-width="2"/>
  
  <!-- 스프링 -->
  <line x1="48" y1="36" x2="48" y2="156" stroke="#E0E0E0" stroke-width="2"/>
  <line x1="52" y1="36" x2="52" y2="156" stroke="#E0E0E0" stroke-width="2"/>
  <line x1="56" y1="36" x2="56" y2="156" stroke="#E0E0E0" stroke-width="2"/>
  
  <!-- 글씨 라인들 -->
  <line x1="66" y1="60" x2="136" y2="60" stroke="#F0F0F0" stroke-width="1"/>
  <line x1="66" y1="72" x2="126" y2="72" stroke="#F0F0F0" stroke-width="1"/>
  <line x1="66" y1="84" x2="140" y2="84" stroke="#F0F0F0" stroke-width="1"/>
  <line x1="66" y1="96" x2="130" y2="96" stroke="#F0F0F0" stroke-width="1"/>
  <line x1="66" y1="108" x2="132" y2="108" stroke="#F0F0F0" stroke-width="1"/>
  <line x1="66" y1="120" x2="142" y2="120" stroke="#F0F0F0" stroke-width="1"/>
  
  <!-- ZBD 텍스트 -->
  <text x="96" y="80" text-anchor="middle" fill="#4A90E2" font-family="Arial, sans-serif" font-size="18" font-weight="bold">ZBD</text>
  
  <!-- 하트 아이콘 -->
  <path d="M96 110 C96 106 94 104 91 104 C88 104 86 106 86 110 C86 106 84 104 81 104 C78 104 76 106 76 110 C76 116 86 126 86 126 C86 126 96 116 96 110 Z" fill="#FF6B6B"/>
</svg>
EOF

# ImageMagick을 사용하여 PNG 아이콘 생성
if command -v convert &> /dev/null; then
    echo "ImageMagick을 사용하여 아이콘 생성 중..."
    
    for dir in "${!sizes[@]}"; do
        size=${sizes[$dir]}
        mkdir -p "/Users/althjs/nst/android-zbd/app/src/main/res/$dir"
        
        # 일반 아이콘
        convert /tmp/zbd_icon.svg -resize ${size}x${size} "/Users/althjs/nst/android-zbd/app/src/main/res/$dir/ic_launcher.png"
        
        # 라운드 아이콘
        convert /tmp/zbd_icon.svg -resize ${size}x${size} \
            \( +clone -threshold 101% -fill white -draw "circle $(($size/2)),$(($size/2)) $(($size/2)),0" \) \
            -alpha off -compose copy_opacity -composite \
            "/Users/althjs/nst/android-zbd/app/src/main/res/$dir/ic_launcher_round.png"
        
        echo "Generated ${size}x${size} icons for $dir"
    done
    
    echo "아이콘 생성 완료!"
else
    echo "ImageMagick이 설치되지 않아 수동으로 PNG 아이콘을 생성해야 합니다."
    echo "SVG 파일은 /tmp/zbd_icon.svg에 저장되었습니다."
fi

# 정리
rm -f /tmp/zbd_icon.svg
