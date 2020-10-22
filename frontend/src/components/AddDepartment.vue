<template>
  <div class="mb-10">
    <form v-on:submit.prevent="createUser()">
      <v-text-field v-model="departmentName" label="Name der Feuerwehr" required />
      <v-text-field v-model="leaderName" label="Jugendwart" required />
      <v-text-field
        v-model="leaderMail"
        type="email"
        label="Jugendwart Email"
        required
      />
      <v-text-field v-model="username" label="Benutzername" required />
      <v-row justify="end">
        <v-btn color="primary" :loading="loading" type="submit">
          <span v-if="created">
            <v-icon medium>mdi-check</v-icon> Erstellt
          </span>
          <span v-if="!created">
            Erstellen
          </span>
        </v-btn>
      </v-row>
    </form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component } from "vue-property-decorator";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  createDepartment
} from "../services/department";

import {
  // eslint-disable-next-line no-unused-vars
  User,
  createUser
} from "../services/user";

@Component({})
export default class AddDepartment extends Vue {
  departmentName = "";
  leaderName = "";
  leaderMail = "";
  username = "";

  loading: boolean = false;
  created: boolean = false;

  createUser() {
    this.loading = true;
    createDepartment(this.departmentName, this.leaderName, this.leaderMail)
      .then(department => createUser(department.id, this.username))
      .then(() => {
        this.loading = false;
        this.created = true;
      })
      .catch(() => (this.loading = false));
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
