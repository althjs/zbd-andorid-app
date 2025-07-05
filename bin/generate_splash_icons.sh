#!/bin/bash

# ZBD 라이프로그 앱 스플래시 아이콘 생성 스크립트
# 루트의 icon.png를 기반으로 안드로이드 스플래시 스크린용 아이콘 생성
# 아이콘이 잘리지 않도록 중앙에 여백을 추가

# 필요한 아이콘 크기들 (디렉토리와 크기 쌍)
sizes=(
    "mipmap-mdpi:48"
    "mipmap-hdpi:72"
    "mipmap-xhdpi:96"
    "mipmap-xxhdpi:144"
    "mipmap-xxxhdpi:192"
)

# 루트의 icon.png 파일 경로
SOURCE_ICON="/Users/althjs/nst/octopus-typing/zbd-andorid-app/icon.png"

# 소스 아이콘 파일 존재 확인
if [[ ! -f "$SOURCE_ICON" ]]; then
    echo "오류: 소스 아이콘 파일을 찾을 수 없습니다: $SOURCE_ICON"
    exit 1
fi

echo "소스 아이콘: $SOURCE_ICON"

# ImageMagick을 사용하여 PNG 아이콘 생성
if command -v convert &> /dev/null; then
    echo "ImageMagick을 사용하여 스플래시 아이콘 생성 중..."
    
    for size_pair in "${sizes[@]}"; do
        dir="${size_pair%:*}"
        size="${size_pair#*:}"
        target_dir="/Users/althjs/nst/octopus-typing/zbd-andorid-app/app/src/main/res/$dir"
        mkdir -p "$target_dir"
        
        # 스플래시 아이콘 생성 (선명도와 안전성의 균형을 위해 70% 크기로 배치)
        # 투명한 배경에 원본 아이콘을 70% 크기로 중앙에 배치 (더 선명하게)
        icon_size=$((size * 7 / 10))  # 70% 크기 (선명도 개선)
        
        convert -size "${size}x${size}" xc:transparent \
                \( "$SOURCE_ICON" -resize "${icon_size}x${icon_size}" -unsharp 0x1.5+1.0+0.01 -enhance \) \
                -gravity center -composite \
                -quality 100 \
                "$target_dir/ic_splash.png"
        
        echo "Generated ${size}x${size} splash icon for $dir (source icon resized to ${icon_size}x${icon_size})"
    done
    
    echo ""
    echo "스플래시 아이콘 생성 완료!"
    echo "이제 앱을 다시 빌드하면 스플래시 화면에서 아이콘이 잘리지 않을 것입니다."
    echo ""
    echo "추가 팁:"
    echo "1. 아이콘이 잘린다면, icon_size 계산에서 7/10을 더 작은 값(예: 3/5)으로 변경하세요."
    echo "2. 아이콘이 너무 작다면, icon_size 계산에서 7/10을 더 큰 값(예: 3/4)으로 변경하세요."
    echo "3. 스플래시 배경색을 조정하려면 colors.xml의 splash_background 색상을 변경하세요."
    echo "4. 더 선명하게 하려면 원본 icon.png를 더 고해상도로 교체하세요."
else
    echo "ImageMagick이 설치되지 않았습니다."
    echo "brew install imagemagick 명령으로 설치하거나,"
    echo "다른 이미지 편집 도구를 사용하여 수동으로 아이콘을 변환하세요."
    echo ""
    echo "수동 변환 가이드:"
    echo "1. $SOURCE_ICON 파일을 열어주세요"
    echo "2. 각 크기별로 투명한 배경에 원본 아이콘을 66% 크기로 중앙에 배치하세요"
    echo "3. 생성된 파일을 app/src/main/res/각 mipmap 폴더에 ic_splash.png로 저장하세요"
fi

echo ""
echo "참고: 안드로이드 스플래시 스크린 아이콘 가이드라인"
echo "- 아이콘은 원형으로 마스킹됩니다"
echo "- 아이콘을 70% 크기로 생성하여 선명도와 안전성의 균형 확보"
echo "- 배경은 투명하게 하고, windowSplashScreenBackground로 배경색을 설정하세요"
echo "- 소스 아이콘: $SOURCE_ICON"
