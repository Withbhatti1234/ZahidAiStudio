# Zahid AI Studio — موبائل سے APK بنانے کا طریقہ

یہ پیکج خاص طور پر اس لیے تیار کیا گیا ہے کہ PC/Laptop نہ ہونے کی صورت میں
GitHub Actions کے ذریعے APK cloud میں build کی جا سکے۔

## ضروری چیز
ایک مفت GitHub account اور موبائل کا browser۔

## طریقہ
1. اس ZIP کو download کریں اور extract کریں۔
2. GitHub پر نیا repository بنائیں، مثال کے طور پر `ZahidAIStudio`.
3. اس project کی تمام files repository میں upload کریں، خاص طور پر `.github/workflows/build-apk.yml` بھی۔
4. GitHub میں **Actions** کھولیں۔
5. **Build Zahid AI Studio APK** workflow منتخب کریں۔
6. **Run workflow** دبائیں۔
7. Build مکمل ہونے کے بعد workflow کے **Artifacts** حصے میں `Zahid-AI-Studio-APK` ملے گا۔
8. APK download کریں، پھر فون میں install کریں۔

## اہم بات
یہ cloud build اصل APK بنائے گا۔ موجودہ app ابھی MVP ہے؛ Samsung/iPhone جیسی
حقیقی generative AI quality کے لیے بعد میں production AI models/backend شامل کرنا ہوگا۔

## Package
`com.zahid.aistudio`
