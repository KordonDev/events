<template>
  <header>
    <img alt="Zeltlager logo" class="hero-image" src="../assets/logo.png" />
    <div id="nav" v-if="loggedIn">
      <router-link to="/">Home</router-link> |
      <router-link to="/login">Login</router-link> |
      <router-link to="/teilnehmer">Teilnehmer</router-link> |
      <router-link to="/files">Anmeldeunterlagen</router-link> |
      <router-link to="/feuerwehr">Meine Feuerwehr</router-link>
      <span v-if="hasAdministrationRole()">
        | <router-link to="/overview">Übersicht</router-link>
      </span>
    </div>
  </header>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";
import {
  getTokenData,
  AuthenticationChangedEvent,
  hasAdministrationRole
} from "../services/authentication";

@Component({})
export default class Header extends Vue {
  timeoutId = 0;
  loggedIn = false;

  hasAdministrationRole = hasAdministrationRole;

  mounted() {
    window.addEventListener("focus", this.checkToken);
    this.checkToken();
    window.addEventListener(AuthenticationChangedEvent, this.checkToken);
  }

  beforeDestroy() {
    this.timeoutId && clearTimeout(this.timeoutId);
    window.removeEventListener("focus", this.checkToken);
    window.removeEventListener(AuthenticationChangedEvent, this.checkToken);
  }

  checkToken() {
    const token = getTokenData();
    if (!token) {
      this.loggedIn = false;
      this.$route.path !== "/login" && this.$router.push("/login");
      return;
    }
    this.loggedIn = true;
    const secondsUntilTokenExpires = token.exp - new Date().getTime() / 1000;
    this.timeoutId = setTimeout(
      () => this.checkToken,
      secondsUntilTokenExpires * 1000
    );
  }
}
</script>

<style scoped>
.hero-image {
  width: 100%;
}
header {
  margin-bottom: 30px;
}
</style>
