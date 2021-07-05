<template>
  <div class="mb-10">
    <form v-on:submit.prevent="createUser()">
      <v-text-field
        v-model="departmentName"
        label="Name der Feuerwehr"
        required
      />
      <v-text-field v-model="leaderName" label="Jugendwart" required />
      <v-text-field
        v-model="leaderMail"
        type="email"
        label="Jugendwart Email"
        required
      />
      <v-text-field v-model="username" label="Benutzername" required />
      <v-alert v-if="error" type="error">
        Es gab einen Fehler beim erzeugen des Benutzers
      </v-alert>
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
import { Component, Prop } from "vue-property-decorator";

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
  @Prop() onDepartmentCreated!: (department: Department) => void;

  departmentName = "";
  leaderName = "";
  leaderMail = "";
  username = "";

  loading: boolean = false;
  created: boolean = false;
  error: boolean = false;

  createUser() {
    this.loading = true;
    this.error = false;
    let newDepartment: Department;
    createDepartment(this.departmentName, this.leaderName, this.leaderMail)
      .then(department => {
        newDepartment = department;
        return createUser(department.id, this.username);
      })
      .then(() => {
        this.loading = false;
        this.created = true;
        this.onDepartmentCreated(newDepartment);
        this.departmentName = "";
        this.leaderName = "";
        this.leaderMail = "";
        this.username = "";
      })
      .catch(() => {
        this.loading = false;
        this.error = true;
      });
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
