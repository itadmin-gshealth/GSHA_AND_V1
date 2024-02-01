package com.omif.gsha.model


class ExpandableEhr {
    companion object {
        fun getData(): HashMap<String, List<String>>? {
            val expandableDetailList = HashMap<String, List<String>>()

            val paediatrics: MutableList<String> = ArrayList()
            paediatrics.add("How to use this App? \n This app can be used in this way")
            paediatrics.add("How to use this App? \n This app can be used in this way")
            paediatrics.add("How to use this App? \n This app can be used in this way")
            paediatrics.add("How to use this App? \n This app can be used in this way")
            paediatrics.add("How to use this App? \n This app can be used in this way")
            val gynaecology: MutableList<String> = ArrayList()
            gynaecology.add("How to use this App? \n This app can be used in this way")
            gynaecology.add("How to use this App? \n This app can be used in this way")
            gynaecology.add("How to use this App? \n This app can be used in this way")
            gynaecology.add("How to use this App? \n This app can be used in this way")
            gynaecology.add("How to use this App? \n This app can be used in this way")
            val dermatology: MutableList<String> = ArrayList()
            dermatology.add("How to use this App? \n This app can be used in this way")
            dermatology.add("How to use this App? \n This app can be used in this way")
            dermatology.add("How to use this App? \n This app can be used in this way")
            dermatology.add("How to use this App? \n This app can be used in this way")
            dermatology.add("How to use this App? \n This app can be used in this way")
            val dentistry: MutableList<String> = ArrayList()
            dentistry.add("How to use this App? \n This app can be used in this way")
            dentistry.add("How to use this App? \n This app can be used in this way")
            dentistry.add("How to use this App? \n This app can be used in this way")
            dentistry.add("How to use this App? \n This app can be used in this way")
            dentistry.add("How to use this App? \n This app can be used in this way")
            val general: MutableList<String> = ArrayList()
            general.add("How to use this App? \n This app can be used in this way")
            general.add("How to use this App? \n This app can be used in this way")
            general.add("How to use this App? \n This app can be used in this way")
            general.add("How to use this App? \n This app can be used in this way")
            general.add("How to use this App? \n This app can be used in this way")


            expandableDetailList["Paediatrics"] = paediatrics
            expandableDetailList["Gynecology"] = gynaecology
            expandableDetailList["Dermatology"] = dermatology
            expandableDetailList["Dentistry"] = dentistry
            expandableDetailList["General Medicine"] = general

            return expandableDetailList
        }
    }
}