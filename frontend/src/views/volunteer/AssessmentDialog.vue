<template>
  <v-dialog v-model="dialog" persistent width="900">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'New Assessment' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Review"
                :rules="[(v) => !!v || 'Review is required']"
                required
                v-model="assessment.review"
                data-cy="reviewInput"
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
          @click="$emit('close-assessment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="isReviewValid()"
          color="blue-darken-1"
          variant="text"
          @click="createAssessment"
          data-cy="saveAssessment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Model, Component, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';
import Activity from '@/models/activity/Activity';

@Component({})
export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  assessment: Assessment = new Assessment();

  cypressCondition: boolean = false;

  async created() {
    this.assessment.review = '';
  }

  isReviewValid() {
    return this.assessment.review.length >= 10;
  }

  get canSave(): boolean {
    return this.cypressCondition || !!this.assessment.review;
  }

  async createAssessment() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        const result = await RemoteServices.registerAssessment(
          this.activity.institution.id,
          this.assessment,
        );
        this.$emit('save-assessment', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
<style scoped lang="scs"></style>
