<template>
  <main id="main__container">
    <Header />
    <div class="form__title">{{ $t('RegisterForm.form__title') }}</div>
    <!-- Ссылка на страницу входа -->
    <div class="link__login__account">
      {{ $t('RegisterForm.login.haveAcc') }}
      <router-link :to="{ name: 'Login' }">{{ $t('RegisterForm.login.signIn') }} </router-link>
    </div>

    <form id="register__form" @submit.prevent="registerSubmit">
      <!-- Поле ввода для Отображаемое имя -->
      <label class="label-form" for="display_name">{{
        $t('RegisterForm.placeholder.display_name')
      }}</label>
      <div class="input__container">
        <input
          id="display_name"
          class="form__input"
          type="text"
          v-model="display_name"
          @input="validation.display_name.$touch()"
        />
        <span v-if="validation.display_name.$error">
          <div class="Errors__Message" v-for="error in display_nameErrors(validation)">
            {{ error }}
          </div>
        </span>
      </div>

      <label class="label-form" for="username">{{ $t('RegisterForm.placeholder.username') }}</label>
      <!-- Поле ввода для имени пользователя -->
      <div class="input__container">
        <input
          autocomplete="off"
          id="username"
          class="form__input"
          type="text"
          v-model="username"
          @input="validation.username.$touch(), (uniqueUsername = false)"
        />
        <span v-if="validation.username.$error">
          <div class="Errors__Message" v-for="error in usernameErrors(validation)">{{ error }}</div>
        </span>
      </div>

      <label class="label-form" for="email">{{ $t('RegisterForm.placeholder.email') }}</label>
      <div class="input__container">
        <!-- Поле ввода для email -->
        <input
          class="form__input"
          v-model="email"
          id="email"
          type="text"
          placeholder="Example@gmail.com"
          @input="validation.email.$touch(), (uniqueEmail = false)"
        />
        <span v-if="validation.email.$error">
          <div class="Errors__Message" v-for="error in emailErrors(validation)">{{ error }}</div>
        </span>
      </div>

      <label class="label-form" for="password">{{ $t('loginForm.placeholder.password') }}</label>
      <div class="input__container">
        <!-- Поле ввода для пароля -->
        <div class="password__field">
          <input
            class="form__input password"
            v-model="password"
            id="password"
            type="password"
            ref="passwordInput"
            @input="validation.password.$touch()"
          />
          <div class="image__wrapper">
            <img
              class="showHidePassword"
              src="../../assets/images/showPassword.svg"
              alt=""
              @click="showHidePassword(this.$refs.passwordInput)"
            />
          </div>
        </div>

        <div v-if="validation.password.$error" class="password-errors-wrapper">
          <div class="title-password-errors">
            {{ $t('RegisterForm.Errors.password.requirements') }}
          </div>

          <div
            v-if="
              validation.password.$model.length > 0 && validation.password.isValidPassword.$invalid
            "
          >
            <div style="margin-left: 0; margin-top: 10px" class="error-text">
              {{ $t('RegisterForm.Errors.password.IncorrectSymbol') }}
            </div>
          </div>
          <div v-else>
            <label
              class="checkbox-container"
              :class="{
                checked:
                  !validation.password.required.$invalid && !validation.password.minLength.$invalid
              }"
            >
              <input type="checkbox" id="1" disabled />
              <div
                :class="{
                  correct:
                    !validation.password.required.$invalid &&
                    !validation.password.minLength.$invalid
                }"
                class="error-text"
              >
                {{ $t('RegisterForm.Errors.password.MinLength') }}
              </div>
              <span class="custom-checkbox"></span>
            </label>

            <label
              class="checkbox-container"
              :class="{
                checked: !validation.password.hasLowerCase.$invalid
              }"
            >
              <input type="checkbox" id="2" disabled />
              <div
                :class="{ correct: !validation.password.hasLowerCase.$invalid }"
                class="error-text"
              >
                {{ $t('RegisterForm.Errors.password.Lowercase') }}
              </div>
              <span class="custom-checkbox"></span>
            </label>

            <label
              class="checkbox-container"
              :class="{ checked: !validation.password.hasUpperCase.$invalid }"
            >
              <input type="checkbox" id="3" disabled />
              <div
                :class="{ correct: !validation.password.hasUpperCase.$invalid }"
                class="error-text"
              >
                {{ $t('RegisterForm.Errors.password.Uppercase') }}
              </div>
              <span class="custom-checkbox"></span>
            </label>

            <label
              class="checkbox-container"
              :class="{ checked: !validation.password.hasSpecialChar.$invalid }"
            >
              <input type="checkbox" id="4" disabled />
              <div
                :class="{ correct: !validation.password.hasSpecialChar.$invalid }"
                class="error-text"
              >
                {{ $t('RegisterForm.Errors.password.SpecialSymbol') }}
              </div>
              <span class="custom-checkbox"></span>
            </label>

            <label
              class="checkbox-container"
              :class="{ checked: !validation.password.hasNumber.$invalid }"
            >
              <input type="checkbox" id="5" />
              <div :class="{ correct: !validation.password.hasNumber.$invalid }" class="error-text">
                {{ $t('RegisterForm.Errors.password.Digit') }}
              </div>
              <span class="custom-checkbox"></span>
              <div class="t-incorrect-password">
                {{ $t('RegisterForm.Errors.password.IncorrectPassword') }}
              </div>
            </label>
          </div>
        </div>
      </div>

      <label class="label-form" for="confirmPassword">{{
        $t('RegisterForm.placeholder.confirmPassword')
      }}</label>
      <div class="input__container">
        <!-- Поле ввода для подтверждения пароля -->
        <div class="password__field">
          <input
            class="form__input password"
            v-model="confirmPassword"
            id="confirmPassword"
            type="password"
            ref="confirmPasswordInput"
            @input="validation.confirmPassword.$touch()"
          />
          <div class="image__wrapper">
            <img
              class="showHidePassword"
              src="../../assets/images/showPassword.svg"
              alt=""
              @click="showHidePassword(this.$refs.confirmPasswordInput)"
            />
          </div>
        </div>
        <span v-if="validation.confirmPassword.$error">
          <div class="Errors__Message" v-for="error in passwordConfirmErrors(validation)">
            {{ error }}
          </div>
        </span>
      </div>

      <div class="acceptPrivacyPolicy">
        <label class="acceptCheckBox">
          <input
            id="acceptCheckBox"
            :class="{ highlight: accept }"
            name="accept"
            type="checkbox"
            v-model="acceptLic"
          />
        </label>
        <label style="padding-left: 10px; color: white" for="acceptCheckBox">
          {{ $t('RegisterForm.accept.IAccept') }}
          <router-link to="#">{{ $t('RegisterForm.accept.terms') }} </router-link>
          {{ $t('RegisterForm.accept.and') }}
          <router-link to="#">{{ $t('RegisterForm.accept.privacy_policy') }} </router-link>
        </label>
      </div>
      <!-- Кнопка отправки формы -->
      <input type="submit" class="submit__button" :value="$t('RegisterForm.buttonSubmit')" />
    </form>
  </main>
</template>

<script lang="js">
import { ref } from 'vue'
import { helpers, maxLength, minLength, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import {
  display_nameErrors,
  emailErrors,
  passwordConfirmErrors,
  usernameErrors
} from '@/services/validator/validationErrors/errorMessages.js'
import { showHidePassword } from '@/utils/showHidePassword.js'
import { register } from '@/utils/query-system/query-actions/authActions.js'
import { saveUserData } from '@/utils/saveUserData.js'
import Header from '@/components/header/Header.vue'
import { createUserProfile, requestGravatar } from '@/utils/query-system/query-actions/userProfileActions.js'
import { useUserProfileStore } from '@/stores/userProfileStore.js'

export default {
  components: { Header },
  methods: {
    display_nameErrors,
    passwordConfirmErrors,
    showHidePassword,
    emailErrors,
    usernameErrors
  },
  setup() {
    let uniqueUsername = ref(false)
    let uniqueEmail = ref(false)
    let accept = ref(false)

    const display_name = ref('')
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const isValidPassword = ref('')

    const acceptLic = ref(false)
    const rules = {
      display_name: {
        required,
        minLength: minLength(3)
      },
      username: {
        required,
        minLength: minLength(3),
        maxLength: maxLength(15),
        regex: helpers.regex(/^[a-zA-Z][a-zA-Z0-9_-]*$/),
        isUnique: (value) => !uniqueUsername.value
      },
      email: {
        required,
        // minLength: minLength(3),
        regex: helpers.regex(
          /(?:[a-zA-Z0-9_]+(?:\.[a-zA-Z0-9_]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])$/
        ),
        isUnique: (value) => !uniqueEmail.value
      },
      password: {
        required,
        minLength: minLength(8),
        hasUpperCase: (value) => /[A-Z]/.test(value), // Проверка на заглавную букву
        hasLowerCase: (value) => /[a-z]/.test(value), // Проверка на строчную букву
        hasDigit: (value) => /\d/.test(value), // Проверка на наличие цифры
        hasSpecialChar: (value) => /[@$!%*?&#]/.test(value), // Проверка на наличие специального символа
        isValidPassword: (value) => /^[A-Za-z0-9@$!%*?&#]+$/.test(value),
        hasNumber: helpers.withMessage('Требуется хотя бы одна цифра', (value) => /\d/.test(value))
      },
      confirmPassword: {
        required,
        sameAsPassword: (value) => value === password.value
      }
    }

    const validation = useVuelidate(rules, {
      username,
      email,
      password,
      display_name,
      confirmPassword
    })

    async function registerSubmit() {
      if (acceptLic.value === true) {
        validation.value.$touch()
        if (!validation.value.$invalid) {
          // если username или email ужа заняты вернет ошибку
          const data = {
            display_name: display_name.value,
            username: username.value,
            email: email.value,
            password: password.value,
            confirmPassword: confirmPassword.value
          }

          try {
            const res = await register(data)
            console.log(res)
            if (res) {
              if (res === 'Email') {
                uniqueEmail.value = true
              } else if (res === 'Username') {
                uniqueUsername.value = true
              } else {
                saveUserData(res, this.rememberMe)

                const createRequest = {username: data.username, displayName: data.display_name};
                const responseUserProfile = await createUserProfile(createRequest);
                useUserProfileStore().setObject(responseUserProfile);

                await requestGravatar();
              }
            }
          } catch (error) {
            console.log(error)
          }
        }
      } else {
        accept.value = true
        setTimeout(() => {
          accept.value = false
        }, 350)
      }
    }

    return {
      display_name,
      confirmPassword,
      isValidPassword,
      username,
      email,
      password,
      acceptLic,
      accept,
      registerSubmit,
      validation,
      uniqueUsername,
      uniqueEmail
    }
  }
}
</script>

<style lang="scss">
@use '@/assets/styles/auth/Registration';
</style>
