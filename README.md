# core
core modules for android applications

## annotations
Содержит аннотации для проектов под  [Darkhitecture](https://ru.wikipedia.org/wiki/%D0%98%D0%BD%D0%BA%D1%83%D0%B1%D0%B0%D1%82%D0%BE%D1%80:Darkhitecture) архитектурой с использованием mvvm. Необходимо использовать совместно с processor модулем.
##### Список и описание аннотаций
| Аннотация     | Описание      |
| ------------- | ------------- |
| CreateFactory | Указывает о необходимости создать фабрику для класса вью модели  |
| ViewModelDiModule  | Указывает о необходимости создания kodein модуля для вью модели  |

## processor
Содержит классы для обработки аннотаций, необходимо использовать совместно с annotations модулем.

## подключение
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
   compileOnly "com.github.VRGsoftUA.core:annotations:$core_version"
   api "com.github.VRGsoftUA.core:processor:$core_version"
   annotationProcessor "com.github.VRGsoftUA.core:processor:$core_version"
}
```
Возможно потребуется дополнительно в app модуле добавить 
```gradle
dependencies {
   annotationProcessor "com.github.VRGsoftUA.core:processor:$core_version"
}
```
## использование
1) подключить библиотеки
2) добавить аннотации
```kotlin
@CreateFactory
@ViewModelDiModule
class TestViewModel()
```
3) использовать сгенерированный класс в фрагменте
```kotlin
class TestFragment : BaseFragment<FragmentTestBinding>() {
   override val viewModelModule = SignInViewModelModule.get(this)
}
```
