 package com.github.trmythos.trmythos.mixin;

import com.github.manasmods.tensura.race.human.HumanRace;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HumanRace.class)
public abstract class HumanRaceMixin {

//    @Inject(method = "getIntrinsicSkills()Ljava/util/List;", at = @At("RETURN"), cancellable = true)
//    private void onGetIntrinsicSkills(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//        List<TensuraSkill> skills = cir.getReturnValue();
//
//        // If the original method returned null, initialize the list
//        if (skills == null) {
//            skills = new ArrayList<>();
//        }
//
//        // Add your custom skills here
//        // For example:
//        skills.add(TRMythosSkills.EXAMPLE_INTRINSIC.get());
//
//        // Set the modified list as the return value
//        cir.setReturnValue(skills);
//    }

//    @Inject(method = "getIntrinsicSkills", at = @AT("RETURN"), cancellable=true)
//    private void addIntrinsicSkill(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//        List<TensuraSkill> skills = cir.getReturnValue();
//        skills.add(TRMythosSkills.EXAMPLE_EXTRA.get());
//    }

//        private final List<TensuraSkill> intrinsicSkills = new ArrayList<>();
//
//        // Add a method to manage intrinsic skills
//        public List<TensuraSkill> getCustomIntrinsicSkills() {
//            if (intrinsicSkills.isEmpty()) {
//                intrinsicSkills.add(TRMythosSkills.EXAMPLE_INTRINSIC.get());
//            }
//            return intrinsicSkills;
//        }

//    @Inject(method = "getIntrinsicSkills", at = @At("RETURN"), cancellable = true, remap = false)
//    private void addCustomSkill(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//        List<TensuraSkill> skills = cir.getReturnValue();
//        skills.add(TRMythosSkills.EXAMPLE_INTRINSIC.get());
//        cir.setReturnValue(skills);
//    }

//    @Inject(method = "getIntrinsicSkills", at = @At("RETURN"), cancellable = true, remap = false)
//    private void overrideIntrinsicSkills(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//        List<TensuraSkill> skills = cir.getReturnValue();
//        skills.add(MyCustomSkills.MY_CUSTOM_HUMAN_SKILL);
//        cir.setReturnValue(skills);
//    }

//        @Inject(method = "getIntrinsicSkills", at = @At("RETURN"), cancellable = true, remap = false)
//        private void overrideIntrinsicSkills(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//            List<TensuraSkill> skills = cir.getReturnValue();
//            skills.add(TRMythosSkills.EXAMPLE_INTRINSIC.get());
//            cir.setReturnValue(skills);
//        }

//    @Inject(method = "getIntrinsicSkills", at = @At("RETURN"), cancellable = true, remap = false)
//    private void modifyIntrinsicSkillsForAllRaces(CallbackInfoReturnable<List<TensuraSkill>> cir) {
//        List<TensuraSkill> skills = cir.getReturnValue();
//
//        // Add a skill conditionally based on the class of the instance
//        if (this instanceof HumanRace) {
//            skills.add(TRMythosSkills.EXAMPLE_INTRINSIC);
//        }
//
//        cir.setReturnValue(skills);
//    }

}
