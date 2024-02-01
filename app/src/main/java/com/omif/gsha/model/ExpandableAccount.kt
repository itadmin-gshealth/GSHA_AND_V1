package com.omif.gsha.model


class ExpandableAccount {
    companion object {
        fun getData(): HashMap<String, List<String>>? {
            val expandableDetailList = HashMap<String, List<String>>()

            val faqs: MutableList<String> = ArrayList()
            faqs.add("How to use this App? \n This app can be used in this way")
            faqs.add("How to use this App? \n This app can be used in this way")
            faqs.add("How to use this App? \n This app can be used in this way")
            faqs.add("How to use this App? \n This app can be used in this way")
            faqs.add("How to use this App? \n This app can be used in this way")
            val contact: MutableList<String> = ArrayList()
            contact.add("Contact Us")
            val sms: MutableList<String> = ArrayList()
            sms.add("SMS")
            val terms: MutableList<String> = ArrayList()
            terms.add("terms and conditions")
            val pp: MutableList<String> = ArrayList()
            pp.add("Privacy Policy")
            val about: MutableList<String> = ArrayList()
            about.add("About GSHA")

            expandableDetailList["FAQs"] = faqs
            expandableDetailList["Contact Us"] = contact
            expandableDetailList["SMS Settings"] = sms
            expandableDetailList["Terms and Conditions"] = terms
            expandableDetailList["Privacy Policy"] = pp
            expandableDetailList["About GSHA"] = about


            return expandableDetailList
        }
    }
}