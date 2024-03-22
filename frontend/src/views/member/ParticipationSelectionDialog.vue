<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Select Participant' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="Rating"
                :rules="[
                  (v) =>
                    isInputValid(v) || 'No Rating or Rating between 1 and 5',
                ]"
                v-model="participation.rating"
                data-cy="ratingInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-participation-selection-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="registerParticipation"
          data-cy="SelectParticipation"
        >
          Make Participant
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>


<script lang="ts">
import {Vue, Component, Model, Prop} from 'vue-property-decorator';
import Participation from '@/models/participation/Participation';
import RemoteServices from '@/services/RemoteServices';

@Component({})
export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  participation: Participation = new Participation();

  cypressCondition: boolean = false;

  async created() {
    this.participation = new Participation();
  }

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.participation.id &&
        !!this.participation.rating &&
        !!this.participation.acceptanceDate &&
        !!this.participation.volunteerId &&
        !!this.participation.activityId)
    );
  }

  isInputValid(value: any) {
    if (value == null || value == '') return true;
    return this.isNumberValid(value);
  }

  isNumberValid(value: any) {
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  async registerParticipation() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        const result = await RemoteServices.registerParticipation(
          this.participation,
        );
        this.$emit('save-participation-selection-dialog', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
