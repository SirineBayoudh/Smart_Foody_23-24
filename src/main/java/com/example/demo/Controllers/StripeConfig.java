package com.example.demo.Controllers;

import com.stripe.Stripe;

public class StripeConfig {
    public static final String STRIPE_SECRET_KEY = "sk_test_51OmIa8LCSLTiPhSoukKSXOFeptQGnE9itVruVs38bDgs6XxPhqF0VhhneZgUuubBwxB3qn1TzIq0TNKwcvRt88yv00Yebkkydb";

    public static void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }
}